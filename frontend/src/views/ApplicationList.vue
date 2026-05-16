<template>
  <div>
    <h2>用车申请列表</h2>
    <div style="margin: 20px 0;">
      <el-button type="primary" @click="loadApplications">刷新列表</el-button>
      <span style="margin-left: 10px; color: #666;">说明：待审批和已批准状态的申请可以点击"修改"按钮进行行程变更</span>
    </div>
    <el-table :data="applications" border stripe style="width: 100%;">
      <el-table-column prop="employeeName" label="申请人" width="100"></el-table-column>
      <el-table-column prop="purpose" label="用途" width="150"></el-table-column>
      <el-table-column prop="destination" label="目的地" width="120"></el-table-column>
      <el-table-column prop="passengers" label="人数" width="80"></el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
      <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
      <el-table-column prop="carPlateNumber" label="车牌号" width="100"></el-table-column>
      <el-table-column prop="driverName" label="司机" width="100"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="emergency" label="紧急" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.emergency" type="danger">是</el-tag>
          <span v-else>否</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" @click="approve(scope.row.id, true)" v-if="scope.row.status === 'PENDING'">批准</el-button>
          <el-button size="mini" type="danger" @click="approve(scope.row.id, false)" v-if="scope.row.status === 'PENDING'">拒绝</el-button>
          <el-button size="mini" type="success" @click="start(scope.row.id)" v-if="scope.row.status === 'APPROVED'">开始</el-button>
          <el-button size="mini" type="warning" @click="openReturnDialog(scope.row)" v-if="scope.row.status === 'IN_PROGRESS'">归还</el-button>
          <el-button size="mini" type="info" @click="edit(scope.row)" v-if="scope.row.status === 'PENDING' || scope.row.status === 'APPROVED'">修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="归还车辆" :visible.sync="returnDialogVisible" width="600px">
      <el-form :model="returnForm" label-width="120px">
        <el-form-item label="车辆">
          <span>{{ returnForm.carInfo }}</span>
        </el-form-item>
        <el-form-item label="当前里程">
          <span>{{ returnForm.startMileage }} km</span>
        </el-form-item>
        <el-form-item label="结束里程">
          <el-input-number v-model="returnForm.endMileage" :min="returnForm.startMileage" @change="calculateFee" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="行驶里程">
          <span style="color: #409EFF; font-weight: bold;">{{ returnForm.distance }} km</span>
        </el-form-item>
        <el-form-item label="油耗(L)">
          <el-input-number v-model="returnForm.fuelUsed" :min="0" @change="calculateFee" :precision="1" style="width: 100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="预估油费">
          <span style="color: #F56C6C; font-weight: bold; font-size: 16px;">¥ {{ returnForm.estimatedFee }}</span>
          <span style="color: #999; margin-left: 10px;">(油价: ¥8.5/L)</span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReturn">确认归还并生成费用</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      applications: [],
      cars: [],
      returnDialogVisible: false,
      returnForm: {
        applicationId: '',
        carId: '',
        carInfo: '',
        startMileage: 0,
        endMileage: 0,
        distance: 0,
        fuelUsed: 0,
        estimatedFee: 0
      }
    }
  },
  mounted() {
    this.loadApplications()
    this.loadCars()
  },
  methods: {
    loadApplications() {
      this.$axios.get('/api/applications')
        .then(response => {
          if (response.data.code === 200) {
            this.applications = response.data.data
          }
        })
    },
    loadCars() {
      this.$axios.get('/api/cars')
        .then(response => {
          if (response.data.code === 200) {
            this.cars = response.data.data
          }
        })
    },
    calculateFee() {
      this.returnForm.distance = this.returnForm.endMileage - this.returnForm.startMileage
      this.returnForm.estimatedFee = (this.returnForm.fuelUsed * 8.5).toFixed(2)
    },
    getStatusType(status) {
      const map = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger',
        'IN_PROGRESS': 'primary',
        'COMPLETED': 'info',
        'CANCELLED': 'info'
      }
      return map[status] || 'info'
    },
    getStatusText(status) {
      const map = {
        'PENDING': '待审批',
        'APPROVED': '已批准',
        'REJECTED': '已拒绝',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return map[status] || status
    },
    approve(id, approved) {
      this.$axios.put(`/api/applications/${id}/approve?approved=${approved}`)
        .then(response => {
          if (response.data.code === 200) {
            const data = response.data.data
            if (data.preempted && approved) {
              this.$message({
                type: 'success',
                message: `审批成功！已抢占 ${data.preemptedCount} 个普通预约`,
                duration: 5000
              })
            } else {
              this.$message.success('操作成功')
            }
            this.loadApplications()
          } else {
            this.$message.error(response.data.message)
          }
        })
    },
    start(id) {
      this.$axios.put(`/api/applications/${id}/start`)
        .then(response => {
          if (response.data.code === 200) {
            this.$message.success('开始用车')
            this.loadApplications()
          } else {
            this.$message.error(response.data.message)
          }
        })
    },
    openReturnDialog(row) {
      const car = this.cars.find(c => c.id === row.carId)
      this.returnForm.applicationId = row.id
      this.returnForm.carId = row.carId
      this.returnForm.carInfo = `${row.carPlateNumber} - ${car.brand} ${car.model}`
      this.returnForm.startMileage = car.currentMileage
      this.returnForm.endMileage = car.currentMileage
      this.returnForm.distance = 0
      this.returnForm.fuelUsed = 0
      this.returnForm.estimatedFee = 0
      this.returnDialogVisible = true
    },
    submitReturn() {
      if (this.returnForm.fuelUsed <= 0) {
        this.$message.warning('请输入油耗')
        return
      }
      this.$axios.post('/api/return-car', this.returnForm)
        .then(response => {
          if (response.data.code === 200) {
            const record = response.data.data
            this.$message.success({
              message: `车辆归还成功！已生成费用记录\n行驶: ${record.distance}km | 油费: ¥${record.fuelCost.toFixed(2)}`,
              duration: 5000
            })
            this.returnDialogVisible = false
            this.loadApplications()
            this.loadCars()
          } else {
            this.$message.error(response.data.message)
          }
        })
    },
    edit(row) {
      this.$router.push({ path: '/create', query: { id: row.id } })
    }
  }
}
</script>
