<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键字">
          <el-input v-model="searchForm.keyword" placeholder="订单号/名称/客户" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="searchForm.orderType" placeholder="请选择" clearable style="width: 130px">
            <el-option label="普通订单" value="1" />
            <el-option label="加急订单" value="2" />
            <el-option label="团体订单" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 130px">
            <el-option label="待确认" value="1" />
            <el-option label="已确认" value="2" />
            <el-option label="制作中" value="3" />
            <el-option label="待复核" value="4" />
            <el-option label="已完成" value="5" />
            <el-option label="已取件" value="6" />
            <el-option label="已取消" value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="付款状态">
          <el-select v-model="searchForm.paymentStatus" placeholder="请选择" clearable style="width: 130px">
            <el-option label="未付款" value="1" />
            <el-option label="部分付款" value="2" />
            <el-option label="已付款" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 260px"
          />
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
          <el-icon><Plus /></el-icon>新增订单
        </el-button>
        <el-button type="success" :disabled="selectedIds.length === 0" @click="handleBatchReview">
          <el-icon><Check /></el-icon>批量复核
        </el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="orderNo" label="订单号" width="160" />
        <el-table-column prop="orderName" label="订单名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="customerName" label="客户名称" width="120" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="orderType" label="订单类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getOrderTypeText(row.orderType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="是否加急" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.urgent === '1' || row.urgent === true" type="danger" size="small">加急</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.orderAmount || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="付款状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getPaymentStatusType(row.paymentStatus)" size="small">
              {{ getPaymentStatusText(row.paymentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expectedDeliveryDate" label="预计交付日期" width="130" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="340" fixed="right">
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
                  <el-dropdown-item command="1">待确认</el-dropdown-item>
                  <el-dropdown-item command="2">已确认</el-dropdown-item>
                  <el-dropdown-item command="3">制作中</el-dropdown-item>
                  <el-dropdown-item command="4">待复核</el-dropdown-item>
                  <el-dropdown-item command="5">已完成</el-dropdown-item>
                  <el-dropdown-item command="6">已取件</el-dropdown-item>
                  <el-dropdown-item command="7">已取消</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button v-if="row.status === '4'" type="success" link @click="handleReview(row)">复核</el-button>
            <el-button type="danger" link @click="handleToggleUrgent(row)">
              {{ row.urgent === '1' || row.urgent === true ? '取消加急' : '加急标记' }}
            </el-button>
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
            <el-form-item label="订单号" prop="orderNo">
              <el-input v-model="formData.orderNo" :disabled="isView || formData.id" placeholder="系统自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单名称" prop="orderName">
              <el-input v-model="formData.orderName" :disabled="isView" placeholder="请输入订单名称" />
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
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="formData.contactPerson" :disabled="isView" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" :disabled="isView" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单类型" prop="orderType">
              <el-select v-model="formData.orderType" :disabled="isView" style="width: 100%">
                <el-option label="普通订单" value="1" />
                <el-option label="加急订单" value="2" />
                <el-option label="团体订单" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单状态" prop="status">
              <el-select v-model="formData.status" :disabled="isView" style="width: 100%">
                <el-option label="待确认" value="1" />
                <el-option label="已确认" value="2" />
                <el-option label="制作中" value="3" />
                <el-option label="待复核" value="4" />
                <el-option label="已完成" value="5" />
                <el-option label="已取件" value="6" />
                <el-option label="已取消" value="7" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="订单金额" prop="orderAmount">
              <el-input-number v-model="formData.orderAmount" :min="0" :precision="2" :disabled="isView" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="加急标记" prop="urgent">
              <el-switch v-model="formData.urgent" :disabled="isView" :active-value="'1'" :inactive-value="'0'" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计交付日期" prop="expectedDeliveryDate">
              <el-date-picker v-model="formData.expectedDeliveryDate" type="date" value-format="YYYY-MM-DD" :disabled="isView" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款状态" prop="paymentStatus">
              <el-select v-model="formData.paymentStatus" :disabled="isView" style="width: 100%">
                <el-option label="未付款" value="1" />
                <el-option label="部分付款" value="2" />
                <el-option label="已付款" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="制作要求" prop="productionRequirement">
              <el-input v-model="formData.productionRequirement" type="textarea" :rows="3" :disabled="isView" placeholder="请输入制作要求" />
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

    <el-dialog v-model="detailVisible" title="订单详情" width="900px" @close="handleDetailClose">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基础信息" name="base">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单名称">{{ detailData.orderName }}</el-descriptions-item>
            <el-descriptions-item label="客户名称">{{ detailData.customerName }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
            <el-descriptions-item label="订单类型">{{ getOrderTypeText(detailData.orderType) }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getStatusType(detailData.status)" size="small">{{ getStatusText(detailData.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="是否加急">
              <el-tag v-if="detailData.urgent === '1' || detailData.urgent === true" type="danger" size="small">加急</el-tag>
              <span v-else>否</span>
            </el-descriptions-item>
            <el-descriptions-item label="订单金额">
              <span class="amount">¥{{ detailData.orderAmount || 0 }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="付款状态">
              <el-tag :type="getPaymentStatusType(detailData.paymentStatus)" size="small">
                {{ getPaymentStatusText(detailData.paymentStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="预计交付日期">{{ detailData.expectedDeliveryDate || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
            <el-descriptions-item label="制作要求" :span="2">{{ detailData.productionRequirement || '-' }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="订单明细" name="items">
          <el-table :data="orderItems" v-loading="itemsLoading" border stripe size="small">
            <el-table-column prop="itemName" label="项目名称" min-width="150" />
            <el-table-column prop="itemType" label="项目类型" width="120" />
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="unitPrice" label="单价" width="120">
              <template #default="{ row }">¥{{ row.unitPrice || 0 }}</template>
            </el-table-column>
            <el-table-column prop="amount" label="金额" width="120">
              <template #default="{ row }">
                <span class="amount">¥{{ row.amount || 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="文件附件" name="files">
          <el-empty description="暂无文件附件" />
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

    <el-dialog v-model="paymentVisible" title="收款登记" width="500px">
      <el-form :model="paymentForm" label-width="100px">
        <el-form-item label="订单金额">
          <span class="amount">¥{{ detailData.orderAmount || 0 }}</span>
        </el-form-item>
        <el-form-item label="已收金额">
          <span>¥{{ detailData.receivedAmount || 0 }}</span>
        </el-form-item>
        <el-form-item label="本次收款" prop="receivedAmount">
          <el-input-number v-model="paymentForm.receivedAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="付款状态" prop="paymentStatus">
          <el-select v-model="paymentForm.paymentStatus" style="width: 100%">
            <el-option label="未付款" value="1" />
            <el-option label="部分付款" value="2" />
            <el-option label="已付款" value="3" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="paymentVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePaymentSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getOrderPage,
  getOrderDetail,
  getOrderItems,
  addOrder,
  updateOrder,
  deleteOrder,
  deleteBatchOrder,
  updateOrderStatus,
  reviewOrder,
  setOrderUrgent,
  updateOrderPayment,
  getOrderOperationLog
} from '@/api/order'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isView = ref(false)
const formRef = ref(null)
const selectedIds = ref([])
const detailVisible = ref(false)
const activeTab = ref('base')
const itemsLoading = ref(false)
const logLoading = ref(false)
const orderItems = ref([])
const operationLogList = ref([])
const detailData = ref({})
const paymentVisible = ref(false)
const paymentForm = reactive({
  receivedAmount: 0,
  paymentStatus: '1'
})

const searchForm = reactive({
  keyword: '',
  orderType: '',
  status: '',
  urgent: '',
  paymentStatus: '',
  customerId: '',
  dateRange: []
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

const formData = reactive({
  id: null,
  orderNo: '',
  orderName: '',
  customerId: '',
  customerName: '',
  contactPerson: '',
  contactPhone: '',
  orderType: '1',
  status: '1',
  orderAmount: 0,
  urgent: '0',
  expectedDeliveryDate: '',
  paymentStatus: '1',
  productionRequirement: '',
  remark: ''
})

const formRules = {
  orderName: [{ required: true, message: '请输入订单名称', trigger: 'blur' }],
  customerName: [{ required: true, message: '请输入客户名称', trigger: 'blur' }],
  orderAmount: [{ required: true, message: '请输入订单金额', trigger: 'blur' }]
}

function getStatusText(status) {
  const map = { '1': '待确认', '2': '已确认', '3': '制作中', '4': '待复核', '5': '已完成', '6': '已取件', '7': '已取消' }
  return map[status] || status
}

function getStatusType(status) {
  const map = { '1': 'warning', '2': 'primary', '3': 'info', '4': 'warning', '5': 'success', '6': 'success', '7': 'info' }
  return map[status] || 'info'
}

function getOrderTypeText(type) {
  const map = { '1': '普通订单', '2': '加急订单', '3': '团体订单' }
  return map[type] || type
}

function getPaymentStatusText(status) {
  const map = { '1': '未付款', '2': '部分付款', '3': '已付款' }
  return map[status] || status
}

function getPaymentStatusType(status) {
  const map = { '1': 'info', '2': 'warning', '3': 'success' }
  return map[status] || 'info'
}

async function fetchData() {
  loading.value = true
  try {
    const params = {
      ...searchForm,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const res = await getOrderPage(params)
    tableData.value = res.data?.list || res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取订单列表失败:', error)
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
  searchForm.orderType = ''
  searchForm.status = ''
  searchForm.urgent = ''
  searchForm.paymentStatus = ''
  searchForm.customerId = ''
  searchForm.dateRange = []
  pagination.pageNum = 1
  fetchData()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

function handleAdd() {
  dialogTitle.value = '新增订单'
  isView.value = false
  resetFormData()
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑订单'
  isView.value = false
  Object.assign(formData, row)
  dialogVisible.value = true
}

function handleView(row) {
  detailData.value = { ...row }
  detailVisible.value = true
  activeTab.value = 'base'
  fetchOrderItems(row.id)
  fetchOperationLog(row.id)
}

async function fetchOrderItems(orderId) {
  itemsLoading.value = true
  try {
    const res = await getOrderItems(orderId)
    orderItems.value = res.data || []
  } catch (error) {
    console.error('获取订单明细失败:', error)
  } finally {
    itemsLoading.value = false
  }
}

async function fetchOperationLog(id) {
  logLoading.value = true
  try {
    const res = await getOrderOperationLog(id)
    operationLogList.value = res.data || []
  } catch (error) {
    console.error('获取操作记录失败:', error)
  } finally {
    logLoading.value = false
  }
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteOrder(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

function handleStatusChange(row, status) {
  ElMessageBox.confirm(`确定要将订单状态变更为【${getStatusText(status)}】吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateOrderStatus(row.id, status, '')
      ElMessage.success('状态变更成功')
      fetchData()
    } catch (error) {
      console.error('状态变更失败:', error)
    }
  }).catch(() => {})
}

function handleReview(row) {
  ElMessageBox.confirm('确定要复核该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await reviewOrder(row.id, '', true)
      ElMessage.success('复核成功')
      fetchData()
    } catch (error) {
      console.error('复核失败:', error)
    }
  }).catch(() => {})
}

function handleBatchReview() {
  ElMessageBox.confirm(`确定要复核选中的 ${selectedIds.value.length} 条订单吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(async () => {
    try {
      await Promise.all(selectedIds.value.map(id => reviewOrder(id, '', true)))
      ElMessage.success('批量复核成功')
      fetchData()
    } catch (error) {
      console.error('批量复核失败:', error)
    }
  }).catch(() => {})
}

function handleToggleUrgent(row) {
  const isUrgent = row.urgent === '1' || row.urgent === true
  const text = isUrgent ? '取消加急' : '加急标记'
  ElMessageBox.confirm(`确定要${text}该订单吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await setOrderUrgent(row.id, !isUrgent ? '1' : '0')
      ElMessage.success(`${text}成功`)
      fetchData()
    } catch (error) {
      console.error('操作失败:', error)
    }
  }).catch(() => {})
}

function handlePayment(row) {
  detailData.value = { ...row }
  paymentForm.receivedAmount = 0
  paymentForm.paymentStatus = row.paymentStatus || '1'
  paymentVisible.value = true
}

async function handlePaymentSubmit() {
  try {
    await updateOrderPayment(detailData.value.id, paymentForm.receivedAmount, paymentForm.paymentStatus)
    ElMessage.success('收款登记成功')
    paymentVisible.value = false
    fetchData()
  } catch (error) {
    console.error('收款登记失败:', error)
  }
}

function resetFormData() {
  formData.id = null
  formData.orderNo = ''
  formData.orderName = ''
  formData.customerId = ''
  formData.customerName = ''
  formData.contactPerson = ''
  formData.contactPhone = ''
  formData.orderType = '1'
  formData.status = '1'
  formData.orderAmount = 0
  formData.urgent = '0'
  formData.expectedDeliveryDate = ''
  formData.paymentStatus = '1'
  formData.productionRequirement = ''
  formData.remark = ''
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateOrder(formData)
          ElMessage.success('更新成功')
        } else {
          await addOrder(formData)
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
  orderItems.value = []
  operationLogList.value = []
}

watch(activeTab, (val) => {
  if (val === 'items' && detailData.value.id && orderItems.value.length === 0) {
    fetchOrderItems(detailData.value.id)
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

.urgent-row {
  background-color: #fef0f0 !important;
}
</style>
