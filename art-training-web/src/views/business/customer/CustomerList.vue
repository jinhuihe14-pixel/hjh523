<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键字">
          <el-input v-model="searchForm.keyword" placeholder="客户名称/编号/电话" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="客户类型">
          <el-select v-model="searchForm.customerType" placeholder="请选择" clearable style="width: 130px">
            <el-option label="个人客户" value="1" />
            <el-option label="企业客户" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="客户等级">
          <el-select v-model="searchForm.level" placeholder="请选择" clearable style="width: 130px">
            <el-option label="普通客户" value="1" />
            <el-option label="VIP客户" value="2" />
            <el-option label="SVIP客户" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 120px">
            <el-option label="正常" value="1" />
            <el-option label="禁用" value="0" />
          </el-select>
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
          <el-icon><Plus /></el-icon>新增客户
        </el-button>
        <el-button type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="customerNo" label="客户编号" width="140" />
        <el-table-column prop="customerName" label="客户名称" min-width="140" />
        <el-table-column prop="customerType" label="客户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.customerType === '1' ? 'success' : 'primary'" size="small">
              {{ row.customerType === '1' ? '个人' : '企业' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="level" label="客户等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)" size="small">{{ getLevelText(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalConsume" label="累计消费" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.totalConsume || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderCount" label="订单数" width="90" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === '1' ? 'success' : 'info'" size="small">
              {{ row.status === '1' ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === '1'" type="warning" link @click="handleToggleStatus(row, '0')">禁用</el-button>
            <el-button v-else type="success" link @click="handleToggleStatus(row, '1')">启用</el-button>
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="650px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="客户名称" prop="customerName">
              <el-input v-model="formData.customerName" placeholder="请输入客户名称" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户类型" prop="customerType">
              <el-select v-model="formData.customerType" placeholder="请选择" style="width: 100%" :disabled="isView">
                <el-option label="个人客户" value="1" />
                <el-option label="企业客户" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="formData.contactPerson" placeholder="请输入联系人" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客户等级" prop="level">
              <el-select v-model="formData.level" placeholder="请选择" style="width: 100%" :disabled="isView">
                <el-option label="普通客户" value="1" />
                <el-option label="VIP客户" value="2" />
                <el-option label="SVIP客户" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择" style="width: 100%" :disabled="isView">
                <el-option label="正常" value="1" />
                <el-option label="禁用" value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系地址" prop="address">
              <el-input v-model="formData.address" placeholder="请输入联系地址" :disabled="isView" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="客户详情" width="900px" @close="handleDetailClose">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础信息" name="base">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="客户编号">{{ detailData.customerNo }}</el-descriptions-item>
            <el-descriptions-item label="客户名称">{{ detailData.customerName }}</el-descriptions-item>
            <el-descriptions-item label="客户类型">{{ getCustomerTypeText(detailData.customerType) }}</el-descriptions-item>
            <el-descriptions-item label="客户等级">{{ getLevelText(detailData.level) }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="累计消费">¥{{ detailData.totalConsume || 0 }}</el-descriptions-item>
            <el-descriptions-item label="订单数">{{ detailData.orderCount || 0 }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="detailData.status === '1' ? 'success' : 'info'" size="small">
                {{ detailData.status === '1' ? '正常' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
            <el-descriptions-item label="联系地址" :span="2">{{ detailData.address || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="消费记录" name="consume">
          <el-table :data="consumeList" v-loading="consumeLoading" border stripe size="small">
            <el-table-column prop="orderNo" label="订单编号" width="160" />
            <el-table-column prop="orderName" label="订单名称" min-width="150" />
            <el-table-column prop="orderAmount" label="订单金额" width="120">
              <template #default="{ row }">
                <span class="amount">¥{{ row.orderAmount }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="orderStatus" label="订单状态" width="100">
              <template #default="{ row }">
                <el-tag size="small">{{ getOrderStatusText(row.orderStatus) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="下单时间" width="180" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="制作偏好" name="preference">
          <el-empty description="暂无制作偏好数据" />
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
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getCustomerPage,
  getCustomerDetail,
  addCustomer,
  updateCustomer,
  deleteCustomer,
  deleteBatchCustomer,
  updateCustomerStatus,
  getCustomerConsumeList,
  getCustomerOperationLog
} from '@/api/customer'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isView = ref(false)
const formRef = ref(null)
const selectedIds = ref([])
const detailVisible = ref(false)
const activeTab = ref('base')
const consumeLoading = ref(false)
const logLoading = ref(false)
const consumeList = ref([])
const operationLogList = ref([])
const detailData = ref({})

const searchForm = reactive({
  keyword: '',
  customerType: '',
  level: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const formData = reactive({
  id: null,
  customerName: '',
  customerType: '1',
  contactPerson: '',
  contactPhone: '',
  level: '1',
  status: '1',
  address: '',
  remark: ''
})

const formRules = {
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  customerType: [{ required: true, message: '请选择客户类型', trigger: 'change' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

function getLevelText(level) {
  const map = { '1': '普通客户', '2': 'VIP客户', '3': 'SVIP客户' }
  return map[level] || '普通客户'
}

function getLevelType(level) {
  const map = { '1': 'info', '2': 'warning', '3': 'danger' }
  return map[level] || 'info'
}

function getCustomerTypeText(type) {
  const map = { '1': '个人客户', '2': '企业客户' }
  return map[type] || type
}

function getOrderStatusText(status) {
  const map = { '1': '待确认', '2': '已确认', '3': '制作中', '4': '待复核', '5': '已完成', '6': '已取件', '7': '已取消' }
  return map[status] || status
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getCustomerPage(params)
    tableData.value = res.data?.list || res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取客户列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchData()
}

function handleReset() {
  searchForm.keyword = ''
  searchForm.customerType = ''
  searchForm.level = ''
  searchForm.status = ''
  pagination.pageNum = 1
  fetchData()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

function handleAdd() {
  dialogTitle.value = '新增客户'
  isView.value = false
  resetFormData()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑客户'
  isView.value = false
  Object.assign(formData, row)
  dialogVisible.value = true
}

function handleView(row) {
  detailData.value = { ...row }
  detailVisible.value = true
  activeTab.value = 'base'
  fetchConsumeList(row.id)
  fetchOperationLog(row.id)
}

async function fetchConsumeList(customerId) {
  consumeLoading.value = true
  try {
    const res = await getCustomerConsumeList(customerId)
    consumeList.value = res.data || []
  } catch (error) {
    console.error('获取消费记录失败:', error)
  } finally {
    consumeLoading.value = false
  }
}

async function fetchOperationLog(id) {
  logLoading.value = true
  try {
    const res = await getCustomerOperationLog(id)
    operationLogList.value = res.data || []
  } catch (error) {
    console.error('获取操作记录失败:', error)
  } finally {
    logLoading.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该客户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCustomer(row.id)
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
      await deleteBatchCustomer(selectedIds.value)
      ElMessage.success('批量删除成功')
      fetchData()
    } catch (error) {
      console.error('批量删除失败:', error)
    }
  }).catch(() => {})
}

function handleToggleStatus(row, status) {
  const text = status === '1' ? '启用' : '禁用'
  ElMessageBox.confirm(`确定要${text}该客户吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateCustomerStatus(row.id, status)
      ElMessage.success(`${text}成功`)
      fetchData()
    } catch (error) {
      console.error('状态更新失败:', error)
    }
  }).catch(() => {})
}

function resetFormData() {
  formData.id = null
  formData.customerName = ''
  formData.customerType = '1'
  formData.contactPerson = ''
  formData.contactPhone = ''
  formData.level = '1'
  formData.status = '1'
  formData.address = ''
  formData.remark = ''
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateCustomer(formData)
          ElMessage.success('更新成功')
        } else {
          await addCustomer(formData)
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
  consumeList.value = []
  operationLogList.value = []
}

watch(activeTab, (val) => {
  if (val === 'consume' && detailData.value.id && consumeList.value.length === 0) {
    fetchConsumeList(detailData.value.id)
  }
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
  gap: 10px;
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.search-form {
  margin-bottom: 20px;
}
</style>
