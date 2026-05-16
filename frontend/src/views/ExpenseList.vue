<template>
  <div>
    <h2>费用记录</h2>
    <div style="margin: 20px 0;">
      <el-button type="primary" @click="loadExpenses">刷新</el-button>
      <span style="margin-left: 10px; color: #666;">说明：归还车辆后系统会自动生成费用记录，包含行驶里程和油耗信息</span>
    </div>
    <el-table :data="expenses" border stripe style="width: 100%;" v-loading="loading">
      <el-table-column prop="id" label="记录编号" width="100"></el-table-column>
      <el-table-column prop="carPlateNumber" label="车牌号" width="120"></el-table-column>
      <el-table-column prop="startMileage" label="起始里程" width="120"></el-table-column>
      <el-table-column prop="endMileage" label="结束里程" width="120"></el-table-column>
      <el-table-column prop="distance" label="行驶里程" width="120">
        <template slot-scope="scope">
          <span style="color: #409EFF; font-weight: bold;">{{ scope.row.distance }} km</span>
        </template>
      </el-table-column>
      <el-table-column prop="fuelUsed" label="油耗(L)" width="120"></el-table-column>
      <el-table-column prop="fuelCost" label="油费" width="120">
        <template slot-scope="scope">
          <span style="color: #F56C6C; font-weight: bold;">¥{{ scope.row.fuelCost.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="totalCost" label="总费用" width="120">
        <template slot-scope="scope">
          <span style="color: #E6A23C; font-weight: bold;">¥{{ scope.row.totalCost.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
    </el-table>
    <el-empty v-if="!loading && expenses.length === 0" description="暂无费用记录，请先完成车辆归还流程" style="margin-top: 50px;"></el-empty>
  </div>
</template>

<script>
export default {
  data() {
    return {
      expenses: [],
      loading: false
    }
  },
  mounted() {
    this.loadExpenses()
  },
  methods: {
    loadExpenses() {
      this.loading = true
      this.$axios.get('/api/expenses')
        .then(response => {
          if (response.data.code === 200) {
            this.expenses = response.data.data
          }
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>
