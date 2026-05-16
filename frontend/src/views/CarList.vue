<template>
  <div>
    <h2>车辆管理</h2>
    <el-table :data="cars" border stripe style="width: 100%; margin-top: 20px;">
      <el-table-column prop="plateNumber" label="车牌号" width="120"></el-table-column>
      <el-table-column prop="brand" label="品牌" width="100"></el-table-column>
      <el-table-column prop="model" label="型号" width="120"></el-table-column>
      <el-table-column prop="seats" label="座位数" width="100"></el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="currentMileage" label="当前里程" width="120"></el-table-column>
      <el-table-column prop="fuelConsumption" label="油耗(L/100km" width="150"></el-table-column>
      <el-table-column prop="lastMaintenanceDate" label="上次保养" width="120"></el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      cars: []
    }
  },
  mounted() {
    this.loadCars()
  },
  methods: {
    loadCars() {
      this.$axios.get('/api/cars')
        .then(response => {
          if (response.data.code === 200) {
            this.cars = response.data.data
          }
        })
    },
    getStatusType(status) {
      const map = {
        'AVAILABLE': 'success',
        'IN_USE': 'primary',
        'MAINTENANCE': 'danger',
        'RESERVED': 'warning'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        'AVAILABLE': '可用',
        'IN_USE': '使用中',
        'MAINTENANCE': '保养中',
        'RESERVED': '已预约'
      }
      return map[status] || status
    }
  }
}
</script>
