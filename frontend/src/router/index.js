import Vue from 'vue'
import Router from 'vue-router'
import ApplicationList from '../views/ApplicationList.vue'
import CreateApplication from '../views/CreateApplication.vue'
import CarList from '../views/CarList.vue'
import DriverList from '../views/DriverList.vue'
import EmployeeList from '../views/EmployeeList.vue'
import ExpenseList from '../views/ExpenseList.vue'
import ScheduledCheck from '../views/ScheduledCheck.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'ApplicationList',
      component: ApplicationList
    },
    {
      path: '/create',
      name: 'CreateApplication',
      component: CreateApplication
    },
    {
      path: '/cars',
      name: 'CarList',
      component: CarList
    },
    {
      path: '/drivers',
      name: 'DriverList',
      component: DriverList
    },
    {
      path: '/employees',
      name: 'EmployeeList',
      component: EmployeeList
    },
    {
      path: '/expenses',
      name: 'ExpenseList',
      component: ExpenseList
    },
    {
      path: '/scheduled',
      name: 'ScheduledCheck',
      component: ScheduledCheck
    }
  ]
})
