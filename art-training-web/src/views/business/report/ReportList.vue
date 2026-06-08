<template>
  <div class="page-container">
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 280px"
          />
        </el-form-item>
        <el-form-item label="客户">
          <el-select v-model="searchForm.customerId" placeholder="请选择客户" clearable filterable style="width: 200px">
            <el-option
              v-for="item in customerOptions"
              :key="item.id"
              :label="item.customerName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="制作类型">
          <el-select v-model="searchForm.type" placeholder="请选择" clearable style="width: 150px">
            <el-option label="全部" value="" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>查询
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>重置
          </el-button>
          <el-button type="success" @click="handleExport">
            <el-icon><Download /></el-icon>导出Excel
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon primary">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总销售额</div>
              <div class="stat-value">¥{{ formatAmount(stats.totalSales) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon success">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">订单总数</div>
              <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon warning">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">客户总数</div>
              <div class="stat-value">{{ stats.totalCustomers || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon info">
              <el-icon><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">合同总数</div>
              <div class="stat-value">{{ stats.totalContracts || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
            </div>
          </template>
          <div ref="salesTrendChartRef" class="chart-container" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>制作类型占比</span>
            </div>
          </template>
          <div ref="typePieChartRef" class="chart-container" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="mt-20">
      <template #header>
        <div class="card-header">
          <span>客户业绩排行榜</span>
        </div>
      </template>
      <el-table :data="customerRankList" v-loading="loading" border stripe>
        <el-table-column type="index" label="排名" width="80">
          <template #default="{ $index }">
            <el-tag v-if="$index < 3" :type="getRankType($index)" size="small">{{ $index + 1 }}</el-tag>
            <span v-else>{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="customerName" label="客户名称" min-width="150" />
        <el-table-column prop="customerNo" label="客户编号" width="160" />
        <el-table-column prop="orderCount" label="订单数" width="100" />
        <el-table-column prop="totalAmount" label="消费金额" width="140">
          <template #default="{ row }">
            <span class="amount">¥{{ formatAmount(row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="avgAmount" label="客单价" width="140">
          <template #default="{ row }">
            ¥{{ formatAmount(row.avgAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastOrderTime" label="最近下单时间" width="180" />
      </el-table>
    </el-card>

    <el-card shadow="hover" class="mt-20">
      <template #header>
        <div class="card-header">
          <span>制作类型统计</span>
        </div>
      </template>
      <el-table :data="typeStatsList" v-loading="typeLoading" border stripe>
        <el-table-column prop="typeName" label="制作类型" min-width="150" />
        <el-table-column prop="orderCount" label="订单数" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="140">
          <template #default="{ row }">
            <span class="amount">¥{{ formatAmount(row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="占比" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="getTypePercentage(row.totalAmount)"
              :stroke-width="12"
              :show-text="true"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onBeforeUnmount, computed } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import {
  getReportOverview,
  getReportByCustomer,
  getReportByType,
  exportOrderReport
} from '@/api/report'
import { getCustomerList } from '@/api/customer'

const loading = ref(false)
const typeLoading = ref(false)
const salesTrendChartRef = ref(null)
const typePieChartRef = ref(null)
let salesTrendChart = null
let typePieChart = null

const searchForm = reactive({
  dateRange: [],
  customerId: '',
  type: ''
})

const stats = reactive({
  totalSales: 0,
  totalOrders: 0,
  totalCustomers: 0,
  totalContracts: 0
})

const customerOptions = ref([])
const customerRankList = ref([])
const typeStatsList = ref([])
const salesTrendData = ref({
  xData: [],
  salesData: [],
  orderData: []
})

const totalTypeAmount = computed(() => {
  return typeStatsList.value.reduce((sum, item) => sum + (item.totalAmount || 0), 0)
})

function formatAmount(amount) {
  if (!amount && amount !== 0) return '0.00'
  return Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function getRankType(index) {
  const types = ['danger', 'warning', 'primary']
  return types[index] || 'info'
}

function getTypePercentage(amount) {
  if (totalTypeAmount.value === 0) return 0
  return Math.round((amount / totalTypeAmount.value) * 100)
}

async function fetchCustomerOptions() {
  try {
    const res = await getCustomerList({ pageSize: 100 })
    customerOptions.value = res.data?.list || res.data?.records || []
  } catch (error) {
    console.error('获取客户列表失败:', error)
  }
}

async function fetchOverview() {
  try {
    const params = {}
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    const res = await getReportOverview(params)
    const data = res.data || {}
    stats.totalSales = data.totalAmount || data.totalSales || 0
    stats.totalOrders = data.totalOrders || 0
    stats.totalCustomers = data.totalCustomers || 0
    stats.totalContracts = data.totalContracts || 0
    
    const trendData = data.salesTrend || []
    salesTrendData.value.xData = trendData.map(item => item.date || item.name || item.month)
    salesTrendData.value.salesData = trendData.map(item => item.amount || item.sales || 0)
    salesTrendData.value.orderData = trendData.map(item => item.orderCount || item.orders || 0)
    updateSalesTrendChart()
  } catch (error) {
    console.error('获取概览数据失败:', error)
  }
}

async function fetchCustomerRank() {
  loading.value = true
  try {
    const params = {}
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    if (searchForm.customerId) {
      params.customerId = searchForm.customerId
    }
    const res = await getReportByCustomer(params)
    customerRankList.value = res.data?.list || res.data?.records || res.data || []
  } catch (error) {
    console.error('获取客户排行失败:', error)
  } finally {
    loading.value = false
  }
}

async function fetchTypeStats() {
  typeLoading.value = true
  try {
    const params = {}
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    if (searchForm.type) {
      params.type = searchForm.type
    }
    const res = await getReportByType(params)
    typeStatsList.value = res.data?.list || res.data?.records || res.data || []
    updateTypePieChart()
  } catch (error) {
    console.error('获取类型统计失败:', error)
  } finally {
    typeLoading.value = false
  }
}

async function handleExport() {
  try {
    const params = {}
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startDate = searchForm.dateRange[0]
      params.endDate = searchForm.dateRange[1]
    }
    if (searchForm.customerId) {
      params.customerId = searchForm.customerId
    }
    if (searchForm.type) {
      params.type = searchForm.type
    }
    
    ElMessage.success('报表导出中，请稍候...')
    const res = await exportOrderReport(params)
    
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `业绩报表_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

function handleSearch() {
  fetchOverview()
  fetchCustomerRank()
  fetchTypeStats()
}

function handleReset() {
  searchForm.dateRange = []
  searchForm.customerId = ''
  searchForm.type = ''
  handleSearch()
}

function initSalesTrendChart() {
  if (!salesTrendChartRef.value) return
  salesTrendChart = echarts.init(salesTrendChartRef.value)
  updateSalesTrendChart()
}

function updateSalesTrendChart() {
  if (!salesTrendChart) return
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['销售额', '订单数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: salesTrendData.value.xData
    },
    yAxis: [
      {
        type: 'value',
        name: '销售额(元)',
        position: 'left'
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        data: salesTrendData.value.salesData,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: '订单数',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: salesTrendData.value.orderData,
        itemStyle: { color: '#67C23A' }
      }
    ]
  }
  salesTrendChart.setOption(option)
}

function initTypePieChart() {
  if (!typePieChartRef.value) return
  typePieChart = echarts.init(typePieChartRef.value)
  updateTypePieChart()
}

function updateTypePieChart() {
  if (!typePieChart) return
  const data = typeStatsList.value.map(item => ({
    name: item.typeName || item.name,
    value: item.totalAmount || item.amount || item.count || 0
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '制作类型',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 18,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ]
  }
  typePieChart.setOption(option)
}

function handleResize() {
  salesTrendChart?.resize()
  typePieChart?.resize()
}

onMounted(async () => {
  await nextTick()
  fetchCustomerOptions()
  fetchOverview()
  fetchCustomerRank()
  fetchTypeStats()
  initSalesTrendChart()
  initTypePieChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  salesTrendChart?.dispose()
  typePieChart?.dispose()
})
</script>

<style scoped lang="scss">
.card-header {
  font-weight: 600;
  font-size: 16px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28px;
    color: #fff;

    &.primary {
      background: linear-gradient(135deg, #409EFF, #66b1ff);
    }
    &.success {
      background: linear-gradient(135deg, #67C23A, #85ce61);
    }
    &.warning {
      background: linear-gradient(135deg, #E6A23C, #ebb563);
    }
    &.info {
      background: linear-gradient(135deg, #909399, #a6a9ad);
    }
  }

  .stat-info {
    .stat-label {
      font-size: 14px;
      color: #909399;
      margin-bottom: 5px;
    }
    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
    }
  }
}

.amount {
  color: #f56c6c;
  font-weight: 600;
}

.mb-20 {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.chart-container {
  width: 100%;
}
</style>
