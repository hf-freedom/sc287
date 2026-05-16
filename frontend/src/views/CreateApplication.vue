<template>
  <div>
    <h2>{{ editMode ? '修改申请' : '新建用车申请' }}</h2>
    
    <el-alert
      v-if="preemptInfo.show"
      :title="'紧急用车将抢占 ' + preemptInfo.count + ' 个普通预约'"
      type="warning"
      show-icon
      style="margin: 20px 0;">
      <div slot="default">
        <p>以下申请的车辆将被您的紧急用车抢占，需要审批后生效：</p>
        <ul>
          <li v-for="app in preemptInfo.apps" :key="app.id">
            {{ app.employeeName }} - {{ app.purpose }} ({{ app.startTime }} ~ {{ app.endTime }})
          </li>
        </ul>
      </div>
    </el-alert>

    <el-form :model="form" label-width="120px" style="max-width: 600px; margin-top: 20px;">
      <el-form-item label="申请人">
        <el-select v-model="form.employeeId" placeholder="请选择" @change="onEmployeeChange">
          <el-option
            v-for="emp in employees"
            :key="emp.id"
            :label="emp.name + ' - ' + emp.level.description + ' (优先级:' + emp.level.priority + ')'"
            :value="emp.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="用途">
        <el-input v-model="form.purpose"></el-input>
      </el-form-item>
      <el-form-item label="目的地">
        <el-input v-model="form.destination"></el-input>
      </el-form-item>
      <el-form-item label="乘车人数">
        <el-input-number v-model="form.passengers" :min="1" style="width: 100%;"></el-input-number>
      </el-form-item>
      <el-form-item label="开始时间">
        <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" style="width: 100%;" value-format="yyyy-MM-dd HH:mm:ss" @change="checkConflict"></el-date-picker>
      </el-form-item>
      <el-form-item label="结束时间">
        <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" style="width: 100%;" value-format="yyyy-MM-dd HH:mm:ss" @change="checkConflict"></el-date-picker>
      </el-form-item>
      <el-form-item label="是否紧急">
        <el-switch v-model="form.emergency" @change="onEmergencyChange"></el-switch>
        <div v-if="form.emergency" style="color: #E6A23C; font-size: 12px; margin-top: 5px;">
          <i class="el-icon-warning"></i> 紧急用车可抢占普通预约，但需要审批
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit" :loading="submitting">{{ editMode ? '修改' : '提交' }}申请</el-button>
        <el-button @click="$router.push('/')">返回</el-button>
      </el-form-item>
    </el-form>

    <el-dialog title="申请提交成功" :visible.sync="resultDialog.show" width="600px">
      <div v-if="resultDialog.isEmergency">
        <el-alert title="紧急用车申请已提交" type="success" show-icon style="margin-bottom: 20px;"></el-alert>
        <p v-if="resultDialog.willPreempt" style="color: #E6A23C; font-weight: bold;">
          <i class="el-icon-warning"></i> 审批通过后将抢占以下 {{ resultDialog.preemptCount }} 个普通预约：
        </p>
        <p v-else style="color: #67C23A;">
          <i class="el-icon-success"></i> 当前时间段资源充足，无需抢占
        </p>
        <el-table :data="resultDialog.preemptibleApps" border stripe style="margin-top: 15px;" v-if="resultDialog.willPreempt">
          <el-table-column prop="employeeName" label="申请人" width="100"></el-table-column>
          <el-table-column prop="purpose" label="用途" width="150"></el-table-column>
          <el-table-column prop="startTime" label="开始时间" width="180"></el-table-column>
          <el-table-column prop="endTime" label="结束时间" width="180"></el-table-column>
        </el-table>
      </div>
      <div v-else>
        <el-alert title="普通用车申请已提交" type="success" show-icon></el-alert>
      </div>
      <div style="text-align: right; margin-top: 20px;">
        <el-button type="primary" @click="$router.push('/')">查看申请列表</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      editMode: false,
      editId: null,
      employees: [],
      submitting: false,
      currentEmployee: null,
      preemptInfo: {
        show: false,
        count: 0,
        apps: []
      },
      resultDialog: {
        show: false,
        isEmergency: false,
        willPreempt: false,
        preemptCount: 0,
        preemptibleApps: []
      },
      form: {
        employeeId: '',
        purpose: '',
        destination: '',
        passengers: 1,
        startTime: null,
        endTime: null,
        emergency: false
      }
    }
  },
  mounted() {
    this.loadEmployees()
    const appId = this.$route.params.id || this.$route.query.id
    if (appId) {
      this.editMode = true
      this.editId = appId
      this.loadApplication()
    }
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
    onEmployeeChange() {
      this.currentEmployee = this.employees.find(e => e.id === this.form.employeeId)
      this.checkConflict()
    },
    onEmergencyChange() {
      this.checkConflict()
    },
    checkConflict() {
      if (!this.form.employeeId || !this.form.startTime || !this.form.endTime) {
        this.preemptInfo.show = false
        return
      }

      if (!this.currentEmployee) {
        this.currentEmployee = this.employees.find(e => e.id === this.form.employeeId)
      }

      this.$axios.get('/api/applications')
        .then(response => {
          if (response.data.code === 200) {
            const applications = response.data.data
            const conflicting = applications.filter(app => {
              if (app.status !== 'APPROVED') return false
              return this.isTimeOverlap(app.startTime, app.endTime, this.form.startTime, this.form.endTime)
            })

            if (this.form.emergency && this.currentEmployee) {
              const preemptible = conflicting.filter(app => 
                !app.emergency && app.employeePriority <= this.currentEmployee.level.priority
              )
              if (preemptible.length > 0) {
                this.preemptInfo = {
                  show: true,
                  count: preemptible.length,
                  apps: preemptible
                }
              } else {
                this.preemptInfo.show = false
              }
            } else {
              this.preemptInfo.show = false
            }
          }
        })
    },
    isTimeOverlap(start1, end1, start2, end2) {
      const s1 = new Date(start1).getTime()
      const e1 = new Date(end1).getTime()
      const s2 = new Date(start2).getTime()
      const e2 = new Date(end2).getTime()
      return s1 < e2 && e1 > s2
    },
    loadApplication() {
      this.$axios.get('/api/applications')
        .then(response => {
          if (response.data.code === 200) {
            const app = response.data.data.find(a => a.id === this.editId)
            if (app) {
              this.form = {
                employeeId: app.employeeId,
                purpose: app.purpose,
                destination: app.destination,
                passengers: app.passengers,
                startTime: app.startTime,
                endTime: app.endTime,
                emergency: app.emergency
              }
              this.onEmployeeChange()
            }
          }
        })
    },
    submit() {
      if (!this.form.employeeId || !this.form.purpose || !this.form.startTime || !this.form.endTime) {
        this.$message.warning('请填写完整信息')
        return
      }

      this.submitting = true
      if (this.editMode) {
        this.$axios.put(`/api/applications/${this.editId}`, this.form)
          .then(response => {
            if (response.data.code === 200) {
              this.$message.success('修改成功')
              this.$router.push('/')
            } else {
              this.$message.error(response.data.message)
            }
          })
          .finally(() => {
            this.submitting = false
          })
      } else {
        this.$axios.post('/api/applications', this.form)
          .then(response => {
            if (response.data.code === 200) {
              const data = response.data.data
              this.resultDialog = {
                show: true,
                isEmergency: this.form.emergency,
                willPreempt: data.willPreempt || false,
                preemptCount: data.preemptCount || 0,
                preemptibleApps: data.preemptibleApps || []
              }
            } else {
              this.$message.error(response.data.message)
            }
          })
          .finally(() => {
            this.submitting = false
          })
      }
    }
  }
}
</script>
