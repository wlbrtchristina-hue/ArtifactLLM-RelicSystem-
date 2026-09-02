<template>
  <div class="home-view">
    <el-card class="welcome-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2><i class="el-icon-house"></i> 欢迎来到文物资源知识管理系统</h2>
        </div>
      </template>
      <div class="welcome-content">
        <p>基于大语言模型的文物知识管理与智能应用平台</p>
        <div class="cta-buttons">
          <el-button type="primary" @click="$router.push('/artifacts')">浏览文物</el-button>
          <el-button type="success" @click="$router.push('/search')">智能检索</el-button>
        </div>
      </div>
    </el-card>

    <el-card class="intro-card" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3><i class="el-icon-info"></i> 系统简介</h3>
        </div>
      </template>
      <div class="intro-content">
        <p>文物资源知识管理系统(ArtifactLLM)是一个基于大语言模型技术的智能化文物知识管理平台。本系统旨在解决传统文物知识管理中存在的信息分散、检索困难、语义理解弱、知识关联不足等问题，通过先进的AI技术提升文物数据的智能化处理能力。</p>
      </div>
    </el-card>

    <el-card class="features-card" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3><i class="el-icon-star-on"></i> 核心功能</h3>
        </div>
      </template>
      <div class="features-grid">
        <el-row :gutter="20">
          <el-col :span="12" :md="6">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="el-icon-folder"></i>
              </div>
              <h4>文物知识库</h4>
              <p>海量文物信息结构化存储，涵盖历史、艺术、科学价值等多个维度</p>
            </div>
          </el-col>
          <el-col :span="12" :md="6">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="el-icon-search"></i>
              </div>
              <h4>智能语义检索</h4>
              <p>基于大语言模型的自然语言检索，精准理解用户查询意图</p>
            </div>
          </el-col>
          <el-col :span="12" :md="6">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="el-icon-share"></i>
              </div>
              <h4>知识图谱</h4>
              <p>构建文物间的关联网络，揭示历史文化脉络</p>
            </div>
          </el-col>
          <el-col :span="12" :md="6">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="el-icon-chat-dot-round"></i>
              </div>
              <h4>智能问答</h4>
              <p>基于文物知识的智能问答系统，提供专业咨询服务</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'HomeView'
}
</script>

<style scoped>
.home-view {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  color: #303133;
}

.card-header i {
  margin-right: 8px;
}

.welcome-content {
  text-align: center;
  padding: 20px 0;
}

.welcome-content p {
  font-size: 1.2rem;
  margin-bottom: 30px;
  color: #606266;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.intro-content p {
  font-size: 1.1rem;
  line-height: 1.6;
  color: #606266;
}

.features-grid {
  padding: 20px 0;
}

.feature-card {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 20px rgba(0,0,0,0.15);
}

.feature-icon {
  font-size: 2.5rem;
  color: #409EFF;
  margin-bottom: 15px;
}

.feature-card h4 {
  color: #303133;
  margin-bottom: 10px;
}

.feature-card p {
  color: #606266;
  line-height: 1.6;
}
</style>