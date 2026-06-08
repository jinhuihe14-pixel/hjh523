<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键字">
          <el-input v-model="searchForm.keyword" placeholder="合同号/名称/客户" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="合同类型">
          <el-select v-model="searchForm.contractType" placeholder="请选择" clearable style="width: 130px">
            <el-option label="服务合同" value="1" />
            <el-option label="采购合同" value="2" />
            <el-option label="框架合同" value="3" />
            <el-option label="其他合同" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 130px">
            <el-option label="未生效" value="1" />
            <el-option label="生效中" value="2" />
            <el-option label="已到期" value="3" />
            <el-option label="已终止" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="searchForm.customerId" placeholder="请输入客户ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-container">
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增合同
        </el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
        <el-alert
          v-if="expiringCount > 0"
          :title="`有 ${expiringCount} 份合同即将到期或已到期，请及时处理`"
          type="warning"
          :closable="false"
          show-icon
          class="expire-alert"
        />
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="contractNo" label="合同编号" width="160" />
        <el-table-column prop="contractName" label="合同名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="customerName" label="客户名称" width="120" />
        <el-table-column prop="contractType" label="合同类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getContractTypeText(row.contractType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="signDate" label="签订日期" width="120" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="contractAmount" label="合同金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.contractAmount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="到期提醒" width="110">
          <template #default="{ row }">
            <el-tag v-if="isExpired(row)" type="danger" size="small">已到期</el-tag>
            <el-tag v-else-if="isExpiringSoon(row)" type="warning" size="small">即将到期</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-dropdown trigger="click" @command="(cmd) => handleStatusChange(row, cmd)">
              <el-button type="warning" link>
                状态变更
                <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="1">未生效</el-dropdown-item>
                  <el-dropdown-item command="2">生效中</el-dropdown-item>
                  <el-dropdown-item command="3">已到期</el-dropdown-item>
                  <el-dropdown-item command="4">已终止</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchData"
          @current-change="fetchData"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" @close="handleDialogClose">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="formData.contractNo" :disabled="isView || formData.id" placeholder="系统自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同名称" prop="contractName">
              <el-input v-model="formData.contractName" :disabled="isView" placeholder="请输入合同名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户ID" prop="customerId">
              <el-input v-model="formData.customerId" :disabled="isView" placeholder="请输入客户ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="formData.customerName" :disabled="isView" placeholder="请输入客户名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同类型" prop="contractType">
              <el-select v-model="formData.contractType" :disabled="isView" style="width: 100%">
                <el-option label="服务合同" value="1" />
                <el-option label="采购合同" value="2" />
                <el-option label="框架合同" value="3" />
                <el-option label="其他合同" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同状态" prop="status">
              <el-select v-model="formData.status" :disabled="isView" style="width: 100%">
                <el-option label="未生效" value="1" />
                <el-option label="生效中" value="2" />
                <el-option label="已到期" value="3" />
                <el-option label="已终止" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同金额" prop="contractAmount">
              <el-input-number v-model="formData.contractAmount" :min="0" :precision="2" :disabled="isView" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="签订日期" prop="signDate">
              <el-date-picker v-model="formData.signDate" type="date" value-format="YYYY-MM-DD" :disabled="isView" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="formData.startDate" type="date" value-format="YYYY-MM-DD" :disabled="isView" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="formData.endDate" type="date" value-format="YYYY-MM-DD" :disabled="isView" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="合同内容" prop="content">
              <el-input v-model="formData.content" type="textarea" :rows="4" :disabled="isView" placeholder="请输入合同内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="formData.remark" type="textarea" :rows="3" :disabled="isView" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="合同详情" width="900px" @close="handleDetailClose">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础信息" name="base">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="合同编号">{{ detailData.contractNo }}</el-descriptions-item>
            <el-descriptions-item label="合同名称">{{ detailData.contractName }}</el-descriptions-item>
            <el-descriptions-item label="客户名称">{{ detailData.customerName }}</el-descriptions-item>
            <el-descriptions-item label="合同类型">{{ getContractTypeText(detailData.contractType) }}</el-descriptions-item>
            <el-descriptions-item label="合同金额">
              <span class="amount">¥{{ detailData.contractAmount || 0 }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(detailData.status)" size="small">
                {{ getStatusText(detailData.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="签订日期">{{ detailData.signDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="开始日期">{{ detailData.startDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="结束日期">{{ detailData.endDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
            <el-descriptions-item label="合同内容" :span="2">{{ detailData.content || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="操作记录" name="log">
          <el-table :data="operationLogList" v-loading="logLoading" border stripe size="small">
            <el-table-column prop="operationTime" label="操作时间" width="180" />
            <el-table-column prop="operationType" label="操作类型" width="100" />
            <el-table-column prop="operationName" label="操作名称" min-width="150" />
            <el-table-column prop="operator" label="操作人" width="100" />
            <el-table-column prop="ip" label="IP地址" width="130" />
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getContractPage,
  getContractDetail,
  addContract,
  updateContract,
  deleteContract,
  deleteBatchContract,
  updateContractStatus,
  getContractOperationLog,
  getExpiringContract
} from '@/api/contract'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isView = ref(false)
const formRef = ref(null)
const selectedIds = ref([])
const detailVisible = ref(false)
const activeTab = ref('base')
const logLoading = ref(false)
const operationLogList = ref([])
const detailData = ref({})
const expiringCount = ref(0)

const searchForm = reactive({
  keyword: '',
  contractType: '',
  status: '',
  customerId: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const formData = reactive({
  id: null,
  contractNo: '',
  contractName: '',
  customerId: '',
  customerName: '',
  contractType: '1',
  status: '1',
  contractAmount: 0,
  signDate: '',
  startDate: '',
  endDate: '',
  content: '',
  remark: ''
})

const formRules = {
  contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  contractAmount: [{ required: true, message: '请输入合同金额', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

function getStatusText(status) {
  const map = { '1': '未生效', '2': '生效中', '3': '已到期', '4': '已终止' }
  return map[status] || status
}

function getStatusType(status) {
  const map = { '1': 'info', '2': 'success', '3': 'warning', '4': 'danger' }
  return map[status] || 'info'
}

function getContractTypeText(type) {
  const map = { '1': '服务合同', '2': '采购合同', '3': '框架合同', '4': '其他合同' }
  return map[type] || type
}

function isExpiringSoon(contract) {
  if (!contract.endDate || contract.status === '3' || contract.status === '4') return false
  const endDate = new Date(contract.endDate)
  const now = new Date()
  const diffDays = Math.ceil((endDate - now) / (1000 * 60 * 60 * 24))
  return diffDays <= 30 && diffDays >= 0
}

function isExpired(contract) {
  if (!contract.endDate || contract.status === '3' || contract.status === '4') return contract.status === '3'
  const endDate = new Date(contract.endDate)
  const now = new Date()
  return now > endDate
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getContractPage(params)
    tableData.value = res.data?.list || res.data?.records || []
    pagination.total = res.data?.total || 0
    fetchExpiringCount()
  } catch (error) {
    console.error('获取合同列表失败:', error)
  } finally {
    loading.value = false
  }
}

async function fetchExpiringCount() {
  try {
    const res = await getExpiringContract(30)
    expiringCount.value = res.data?.length || 0
  } catch (error) {
    console.error('获取即将到期合同数量失败:', error)
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchData()
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.contractType = ''
  searchForm.status = ''
  searchForm.customerId = ''
  pagination.pageNum = 1
  fetchData()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

function handleAdd() {
  dialogTitle.value = '新增合同'
  isView.value = false
  resetFormData()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑合同'
  isView.value = false
  Object.assign(formData, row)
  dialogVisible.value = true
}

function handleView(row) {
  detailData.value = { ...row }
  detailVisible.value = true
  activeTab.value = 'base'
  fetchOperationLog(row.id)
}

async function fetchOperationLog(id) {
  logLoading.value = true
  try {
    const res = await getContractOperationLog(id)
    operationLogList.value = res.data || []
  } catch (error) {
    console.error('获取操作记录失败:', error)
  } finally {
    logLoading.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该合同吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteContract(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

function handleBatchDelete() {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBatchContract(selectedIds.value)
      ElMessage.success('批量删除成功')
      fetchData()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  }).catch(() => {})
}

function handleStatusChange(row, status) {
  ElMessageBox.confirm(`确定要将合同状态变更为【${getStatusText(status)}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateContractStatus(row.id, status)
      ElMessage.success('状态变更成功')
      fetchData()
    } catch (error) {
      console.error('状态变更失败:', error)
    }
  }).catch(() => {})
}

function resetFormData() {
  formData.id = null
  formData.contractNo = ''
  formData.contractName = ''
  formData.customerId = ''
  formData.customerName = ''
  formData.contractType = '1'
  formData.status = '1'
  formData.contractAmount = 0
  formData.signDate = ''
  formData.startDate = ''
  formData.endDate = ''
  formData.content = ''
  formData.remark = ''
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateContract(formData)
          ElMessage.success('更新成功')
        } else {
          await addContract(formData)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error('提交失败:', error)
      }
    }
  })
}

function handleDialogClose() {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  resetFormData()
}

function handleDetailClose() {
  detailData.value = {}
  operationLogList.value = []
}

watch(activeTab, (val) => {
  if (val === 'log' && detailData.value.id && operationLogList.value.length === 0) {
    fetchOperationLog(detailData.value.id)
  }
})

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.table-toolbar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;

  .expire-alert {
    flex: 1;
    margin-left: 10px;
  }
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.search-form {
  margin-bottom: 20px;
}

:deep(.el-table__row.warning-row) {
  background-color: #fdf6ec !important;
}

:deep(.el-table__row.danger-row) {
  background-color: #fef0f0 !important;
}
</style>
