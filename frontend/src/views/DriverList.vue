<template>
  <div>
    <h2>司机管理</h2>
    <el-table :data="drivers" border stripe style="width: 100%; margin-top: 20px;">
      <el-table-column prop="name" label="姓名" width="120"></el-table-column>
      <el-table-column prop="phone" label="电话" width="150"></el-table-column>
      <el-table-column prop="licenseNumber" label="驾驶证号" width="180"></el-table-column>
      <el-table-column prop="available" label="状态" width="120">
        <template slot-scope="scope">
          <el-tag :type="scope.row.available ? 'success' : 'danger'">
            {{ scope.row.available ? '可用' : '不可用' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      drivers: []
    }
  },
  mounted() {
    this.loadDrivers()
  },
  methods: {
    loadDrivers() {
      this.$axios.get('/api/drivers')
        .then(response => {
          if (response.data.code === 200) {
            this.drivers = response.data.data
          }
        })
    }
  }
}
</script>
