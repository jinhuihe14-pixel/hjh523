<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="业务类型">
          <el-select v-model="searchForm.businessType" placeholder="请选择" clearable style="width: 150px">
            <el-option label="客户管理" value="customer" />
            <el-option label="订单管理" value="order" />
            <el-option label="合同管理" value="contract" />
            <el-option label="系统管理" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="请选择" clearable style="width: 130px">
            <el-option label="新增" value="add" />
            <el-option label="编辑" value="edit" />
            <el-option label="删除" value="delete" />
            <el-option label="查询" value="query" />
            <el-option label="导出" value="export" />
            <el-option label="状态变更" value="status" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="searchForm.operator" placeholder="请输入操作人" clearable style="width: 150px" />
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
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="operationTime" label="操作时间" width="180" />
        <el-table-column prop="businessType" label="业务类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getBusinessTypeColor(row.businessType)" size="small">
              {{ getBusinessTypeText(row.businessType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="businessNo" label="业务编号" width="160" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeColor(row.operationType)" size="small">
              {{ getOperationTypeText(row.operationType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationName" label="操作名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
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

    <el-dialog v-model="dialogVisible" title="操作详情" width="650px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作时间" :span="2">{{ detailData.operationTime }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ getBusinessTypeText(detailData.businessType) }}</el-descriptions-item>
        <el-descriptions-item label="业务编号">{{ detailData.businessNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ getOperationTypeText(detailData.operationType) }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.operator }}</el-descriptions-item>
        <el-descriptions-item label="操作名称" :span="2">{{ detailData.operationName }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detailData.ip }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ detailData.method || '-' }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre class="json-content">{{ formatJson(detailData.params) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回结果" :span="2">
          <pre class="json-content">{{ formatJson(detailData.result) }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="耗时">{{ detailData.costTime ? detailData.costTime + 'ms' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.success === false ? 'danger' : 'success'" size="small">
            {{ detailData.success === false ? '失败' : '成功' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="异常信息" :span="2" v-if="detailData.errorMsg">
          <pre class="error-content">{{ detailData.errorMsg }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOperationLogPage } from '@/api/report'

const loading = ref(false)
const dialogVisible = ref(false)
const detailData = ref({})

const searchForm = reactive({
  businessType: '',
  operationType: '',
  operator: '',
  dateRange: []
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const tableData = ref([])

function getBusinessTypeText(type) {
  const map = {
    customer: '客户管理',
    order: '订单管理',
    contract: '合同管理',
    system: '系统管理',
    report: '报表中心'
  }
  return map[type] || type
}

function getBusinessTypeColor(type) {
  const map = {
    customer: 'primary',
    order: 'success',
    contract: 'warning',
    system: 'info',
    report: 'danger'
  }
  return map[type] || 'info'
}

function getOperationTypeText(type) {
  const map = {
    add: '新增',
    edit: '编辑',
    delete: '删除',
    query: '查询',
    export: '导出',
    status: '状态变更',
    login: '登录',
    logout: '登出'
  }
  return map[type] || type
}

function getOperationTypeColor(type) {
  const map = {
    add: 'success',
    edit: 'warning',
    delete: 'danger',
    query: 'primary',
    export: 'info',
    status: 'warning',
    login: 'success',
    logout: 'info'
  }
  return map[type] || 'info'
}

function formatJson(data) {
  if (!data) return '无'
  if (typeof data === 'string') {
    try {
      return JSON.stringify(JSON.parse(data), null, 2)
    } catch (e) {
      return data
    }
  }
  return JSON.stringify(data, null, 2)
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
      params.startTime = searchForm.dateRange[0]
      params.endTime = searchForm.dateRange[1]
    }
    const res = await getOperationLogPage(params)
    tableData.value = res.data?.list || res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (error) {
    console.error('获取操作记录失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pagination.pageNum = 1
  fetchData()
}

function handleReset() {
  searchForm.businessType = ''
  searchForm.operationType = ''
  searchForm.operator = ''
  searchForm.dateRange = []
  pagination.pageNum = 1
  fetchData()
}

function handleView(row) {
  detailData.value = { ...row }
  dialogVisible.value = true
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped lang="scss">
.search-form {
  margin-bottom: 20px;
}

.json-content {
  margin: 0;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  font-family: 'Courier New', monospace;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
  color: #606266;
}

.error-content {
  margin: 0;
  padding: 12px;
  background: #fef0f0;
  border-radius: 4px;
  font-size: 12px;
  font-family: 'Courier New', monospace;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 200px;
  overflow-y: auto;
  color: #f56c6c;
}
</style>
