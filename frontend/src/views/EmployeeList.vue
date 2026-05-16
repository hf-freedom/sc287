<template>
  <div>
    <h2>员工列表</h2>
    <el-table :data="employees" border stripe style="width: 100%; margin-top: 20px;">
      <el-table-column prop="name" label="姓名" width="120"></el-table-column>
      <el-table-column prop="department" label="部门" width="150"></el-table-column>
      <el-table-column label="级别" width="150">
        <template slot-scope="scope">
          <el-tag :type="getLevelType(scope.row.level.priority)">
            {{ scope.row.level.description }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="优先级" width="100">
        <template slot-scope="scope">
          {{ scope.row.level.priority }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
export default {
  data() {
    return {
      employees: []
    }
  },
  mounted() {
    this.loadEmployees()
  },
  methods: {
    loadEmployees() {
      this.$axios.get('/api/employees')
        .then(response => {
          if (response.data.code === 200) {
            this.employees = response.data.data
          }
        })
    },
    getLevelType(priority) {
      const map = {
        1: 'info',
        2: 'success',
        3: 'warning',
        4: 'danger'
      }
      return map[priority] || 'info'
    }
  }
}
</script>
