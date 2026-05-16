<template>
  <div>
    <h2>定时检查结果</h2>
    <el-button type="primary" @click="loadData" style="margin: 20px 0;">刷新</el-button>

    <el-card shadow="hover" style="margin-bottom: 20px;">
      <div slot="header">
        <span style="color: #409EFF; font-weight: bold;">即将出车（2小时内）</span>
      </div>
      <el-table :data="checkData.upcomingDepartures || []" border stripe v-if="checkData.upcomingDepartures && checkData.upcomingDepartures.length > 0">
        <el-table-column prop="employeeName" label="申请人" width="120"></el-table-column>
        <el-table-column prop="carPlateNumber" label="车牌号" width="120"></el-table-column>
        <el-table-column prop="driverName" label="司机" width="120"></el-table-column>
        <el-table-column prop="startTime" label="出车时间" width="180"></el-table-column>
        <el-table-column prop="purpose" label="用途"></el-table-column>
      </el-table>
      <el-empty v-else description="暂无即将出车的申请"></el-empty>
    </el-card>

    <el-card shadow="hover" style="margin-bottom: 20px;">
      <div slot="header">
        <span style="color: #F56C6C; font-weight: bold;">逾期未还</span>
      </div>
      <el-table :data="checkData.overdueReturns || []" border stripe v-if="checkData.overdueReturns && checkData.overdueReturns.length > 0">
        <el-table-column prop="employeeName" label="申请人" width="120"></el-table-column>
        <el-table-column prop="carPlateNumber" label="车牌号" width="120"></el-table-column>
        <el-table-column prop="driverName" label="司机" width="120"></el-table-column>
        <el-table-column prop="endTime" label="应还时间" width="180"></el-table-column>
        <el-table-column prop="purpose" label="用途"></el-table-column>
      </el-table>
      <el-empty v-else description="暂无逾期未还车辆"></el-empty>
    </el-card>

    <el-card shadow="hover">
      <div slot="header">
        <span style="color: #E6A23C; font-weight: bold;">保养到期（1周内）</span>
      </div>
      <el-table :data="checkData.maintenanceDue || []" border stripe v-if="checkData.maintenanceDue && checkData.maintenanceDue.length > 0">
        <el-table-column prop="plateNumber" label="车牌号" width="120"></el-table-column>
        <el-table-column prop="brand" label="品牌" width="100"></el-table-column>
        <el-table-column prop="model" label="型号" width="120"></el-table-column>
        <el-table-column prop="lastMaintenanceDate" label="上次保养时间" width="180"></el-table-column>
        <el-table-column prop="currentMileage" label="当前里程" width="120"></el-table-column>
      </el-table>
      <el-empty v-else description="暂无即将保养车辆"></el-empty>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      checkData: {}
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.$axios.get('/api/scheduled-check')
        .then(response => {
          if (response.data.code === 200) {
            this.checkData = response.data.data
          }
        })
    }
  }
}
</script>
