<template>
  <div class="page-container">
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon primary">
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
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">总金额</div>
              <div class="stat-value">¥{{ formatAmount(stats.totalAmount) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">加急订单</div>
              <div class="stat-value">{{ stats.urgentOrders || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon info">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">客户数量</div>
              <div class="stat-value">{{ stats.totalCustomers || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="chartPeriod" size="small" @change="fetchSalesTrend">
                <el-radio-button value="week">本周</el-radio-button>
                <el-radio-button value="month">本月</el-radio-button>
                <el-radio-button value="year">本年</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="salesChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>制作类型占比</span>
            </div>
          </template>
          <div ref="typePieChartRef" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="hover" class="mt-20">
      <template #header>
        <div class="card-header">
          <span>即将到期合同</span>
          <el-button type="primary" link @click="goToContract">查看全部</el-button>
        </div>
      </template>
      <el-table :data="expiringContracts" v-loading="expiringLoading" border stripe size="small">
        <el-table-column prop="contractNo" label="合同编号" width="160" />
        <el-table-column prop="contractName" label="合同名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="customerName" label="客户名称" width="120" />
        <el-table-column prop="endDate" label="到期日期" width="120" />
        <el-table-column label="剩余天数" width="100">
          <template #default="{ row }">
            <el-tag :type="getDaysLeftType(row)" size="small">{{ getDaysLeft(row) }}天</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="contractAmount" label="合同金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.contractAmount || 0 }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getReportOverview, getReportByType } from '@/api/report'
import { getExpiringContract } from '@/api/contract'

const router = useRouter()
const chartPeriod = ref('month')
const salesChartRef = ref(null)
const typePieChartRef = ref(null)
let salesChart = null
let typePieChart = null
const expiringLoading = ref(false)

const stats = reactive({
  totalOrders: 0,
  totalAmount: 0,
  urgentOrders: 0,
  totalCustomers: 0
})

const expiringContracts = ref([])

const salesTrendData = ref({
  xData: [],
  salesData: [],
  orderData: []
})

const typeData = ref([])

function formatAmount(amount) {
  if (!amount) return '0.00'
  return Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function getDaysLeft(contract) {
  if (!contract.endDate) return 0
  const endDate = new Date(contract.endDate)
  const now = new Date()
  const diffDays = Math.ceil((endDate - now) / (1000 * 60 * 60 * 24))
  return diffDays
}

function getDaysLeftType(contract) {
  const days = getDaysLeft(contract)
  if (days <= 7) return 'danger'
  if (days <= 30) return 'warning'
  return 'success'
}

function goToContract() {
  router.push('/business/contract')
}

async function fetchOverview() {
  try {
    const res = await getReportOverview()
    const data = res.data || {}
    stats.totalOrders = data.totalOrders || 0
    stats.totalAmount = data.totalAmount || 0
    stats.urgentOrders = data.urgentOrders || 0
    stats.totalCustomers = data.totalCustomers || 0
  } catch (error) {
    console.error('获取概览数据失败:', error)
  }
}

async function fetchSalesTrend() {
  try {
    const res = await getReportOverview({ period: chartPeriod.value })
    const data = res.data?.salesTrend || []
    salesTrendData.value.xData = data.map(item => item.date || item.name)
    salesTrendData.value.salesData = data.map(item => item.amount || item.sales || 0)
    salesTrendData.value.orderData = data.map(item => item.orderCount || item.orders || 0)
    updateSalesChart()
  } catch (error) {
    console.error('获取销售趋势失败:', error)
  }
}

async function fetchTypeData() {
  try {
    const res = await getReportByType()
    typeData.value = res.data || []
    updateTypeChart()
  } catch (error) {
    console.error('获取制作类型数据失败:', error)
  }
}

async function fetchExpiringContracts() {
  expiringLoading.value = true
  try {
    const res = await getExpiringContract(30)
    expiringContracts.value = res.data || []
  } catch (error) {
    console.error('获取即将到期合同失败:', error)
  } finally {
    expiringLoading.value = false
  }
}

function initSalesChart() {
  if (!salesChartRef.value) return
  salesChart = echarts.init(salesChartRef.value)
  updateSalesChart()
}

function updateSalesChart() {
  if (!salesChart) return
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单数'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
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
  salesChart.setOption(option)
}

function initTypeChart() {
  if (!typePieChartRef.value) return
  typePieChart = echarts.init(typePieChartRef.value)
  updateTypeChart()
}

function updateTypeChart() {
  if (!typePieChart) return
  const data = typeData.value.map(item => ({
    name: item.typeName || item.name,
    value: item.count || item.amount || 0
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
  salesChart?.resize()
  typePieChart?.resize()
}

onMounted(async () => {
  await nextTick()
  fetchOverview()
  fetchSalesTrend()
  fetchTypeData()
  fetchExpiringContracts()
  initSalesChart()
  initTypeChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  typePieChart?.dispose()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    gap: 20px;

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 30px;
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
      flex: 1;

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
</style>
