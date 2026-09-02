<template>
  <div class="about">
    <div class="page-header">
      <h1>关于我们</h1>
      <p>了解文物资源知识管理系统及其开发团队</p>
    </div>
    
    <div class="about-section">
      <div class="section-header">
        <h2>项目简介</h2>
      </div>
      <div class="section-content">
        <p>文物资源知识管理系统(ArtifactLLM)是一个基于大语言模型技术的智能化文物知识管理平台。本系统旨在解决传统文物知识管理中存在的信息分散、检索困难、语义理解弱、知识关联不足等问题，通过先进的AI技术提升文物数据的智能化处理能力。</p>
      </div>
    </div>
    
    <div class="about-section">
      <div class="section-header">
        <h2>核心功能</h2>
      </div>
      <div class="section-content">
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-cloud-download-alt"></i>
            </div>
            <h3>文物知识自动化采集</h3>
            <p>支持多源异构文物数据的自动化采集与结构化处理</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-search"></i>
            </div>
            <h3>智能语义检索</h3>
            <p>基于大语言模型的自然语言问答与语义检索</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-project-diagram"></i>
            </div>
            <h3>知识图谱构建</h3>
            <p>多维度文物知识图谱构建与可视化展示</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-users"></i>
            </div>
            <h3>团队协作管理</h3>
            <p>支持团队协作开发与项目进度管理</p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="about-section">
      <div class="section-header">
        <h2>技术架构</h2>
      </div>
      <div class="section-content">
        <div class="architecture-diagram">
          <div class="diagram-placeholder">
            <p>系统架构图</p>
          </div>
        </div>
        <div class="tech-stack">
          <div class="tech-item">
            <div class="tech-icon frontend"></div>
            <h4>前端技术</h4>
            <p>Vue 3 + Vite</p>
          </div>
          <div class="tech-item">
            <div class="tech-icon backend"></div>
            <h4>后端技术</h4>
            <p>Spring Boot</p>
          </div>
          <div class="tech-item">
            <div class="tech-icon ai"></div>
            <h4>AI框架</h4>
            <p>大语言模型</p>
          </div>
          <div class="tech-item">
            <div class="tech-icon database"></div>
            <h4>数据库</h4>
            <p>MySQL + 向量数据库</p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="about-section">
      <div class="section-header">
        <h2>开发团队</h2>
      </div>
      <div class="section-content">
        <div class="team-members">
          <div class="team-member" v-for="member in teamMembers" :key="member.name">
            <div class="member-avatar">
              <i class="fas fa-user"></i>
            </div>
            <h4>{{ member.name }}</h4>
            <p class="member-role">{{ member.role }}</p>
            <p class="member-desc">{{ member.description }}</p>
          </div>
        </div>
      </div>
    </div>
    
    <div class="contact-section">
      <div class="section-header">
        <h2>联系我们</h2>
      </div>
      <div class="section-content">
        <div class="contact-info">
          <p>如果您对本系统有任何建议或意见，欢迎与我们联系：</p>
          <div class="contact-methods">
            <div class="contact-item">
              <i class="fas fa-envelope"></i>
              <span>邮箱: artifactllm@example.com</span>
            </div>
            <div class="contact-item">
              <i class="fas fa-phone"></i>
              <span>电话: 400-123-4567</span>
            </div>
            <div class="contact-item">
              <i class="fas fa-map-marker-alt"></i>
              <span>地址: 北京市海淀区文物技术研究院</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AboutView',
  data() {
    return {
      teamMembers: [
        {
          name: '哈俊元',
          role: '前端开发工程师',
          description: '负责系统前端界面设计与开发，用户体验优化'
        },
        {
          name: '符晋康',
          role: '后端开发工程师',
          description: '负责系统后端架构设计与API开发'
        },
        {
          name: '李梦源',
          role: '全栈开发工程师',
          description: '负责系统核心功能模块开发与集成'
        },
        {
          name: '袁明霜涛',
          role: '系统架构师',
          description: '负责系统整体架构设计与技术选型'
        }
      ]
    }
  }
}
</script>

<style scoped>
.about {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  color: #2c3e50;
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.page-header p {
  color: #666;
  font-size: 1.1rem;
}

.about-section {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
  overflow: hidden;
}

.section-header {
  background: #2c3e50;
  color: white;
  padding: 1rem 1.5rem;
}

.section-header h2 {
  margin: 0;
  font-size: 1.3rem;
}

.section-content {
  padding: 1.5rem;
}

.section-content p {
  color: #555;
  line-height: 1.6;
  font-size: 1.1rem;
}

.project-image {
  text-align: center;
  margin-top: 1.5rem;
}

.project-image img {
  max-width: 100%;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 1.5rem;
}

.feature-card {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
  text-align: center;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 2.5rem;
  color: #42b983;
  margin-bottom: 1rem;
}

.feature-card h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}

.architecture-diagram {
  margin-bottom: 2rem;
}

.diagram-placeholder {
  background: #f8f9fa;
  border: 2px dashed #dee2e6;
  border-radius: 10px;
  padding: 2rem;
  text-align: center;
  color: #666;
}

.tech-stack {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
}

.tech-item {
  text-align: center;
  padding: 1.5rem;
  background: white;
  border: 1px solid #ddd;
  border-radius: 10px;
}

.tech-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 1rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
}

.frontend {
  background: linear-gradient(135deg, #42b983 0%, #359c6d 100%);
}

.backend {
  background: linear-gradient(135deg, #667eea 0%, #5a6fd8 100%);
}

.ai {
  background: linear-gradient(135deg, #764ba2 0%, #6a4390 100%);
}

.database {
  background: linear-gradient(135deg, #f67c11 0%, #e56f0a 100%);
}

.tech-item h4 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.tech-item p {
  color: #666;
  margin: 0;
}

.team-members {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 2rem;
}

.team-member {
  text-align: center;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.member-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: #2c3e50;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin: 0 auto 1rem;
}

.team-member h4 {
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.member-role {
  color: #42b983;
  font-weight: bold;
  margin: 0 0 0.5rem 0;
}

.member-desc {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.5;
  margin: 0;
}

.contact-section {
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  margin-bottom: 2rem;
  overflow: hidden;
}

.contact-methods {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 5px;
}

.contact-item i {
  color: #42b983;
  font-size: 1.2rem;
  width: 24px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .features-grid,
  .tech-stack,
  .team-members {
    grid-template-columns: 1fr;
  }
  
  .contact-methods {
    align-items: center;
  }
  
  .contact-item {
    flex-direction: column;
    text-align: center;
    gap: 0.5rem;
  }
}
</style>