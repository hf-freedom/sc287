<template>
  <div id="app">
    <el-container style="height: 100vh;">
      <el-header style="background: #409EFF; color: white; font-size: 24px; line-height: 60px; padding: 0 20px;">
        企业用车申请系统
      </el-header>
      <el-container>
        <el-aside width="200px" style="background: #f5f5f5;">
          <el-menu
            :default-active="$route.path"
            router
            style="border: none;"
          >
            <el-menu-item index="/">申请列表</el-menu-item>
            <el-menu-item index="/create">新建申请</el-menu-item>
            <el-menu-item index="/cars">车辆管理</el-menu-item>
            <el-menu-item index="/drivers">司机管理</el-menu-item>
            <el-menu-item index="/employees">员工列表</el-menu-item>
            <el-menu-item index="/expenses">费用记录</el-menu-item>
            <el-menu-item index="/scheduled">定时检查</el-menu-item>
          </el-menu>
        </el-aside>
        <el-main style="background: #fff; padding: 20px;">
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}
</style>
