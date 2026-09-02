<template>
  <div class="knowledge-graph-view">
    <!-- 图谱控制卡片 -->
    <el-card class="controls-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2><i class="el-icon-setting"></i> 图谱控制</h2>
        </div>
      </template>
      
      <el-form :inline="true" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="中心文物">
              <el-select v-model="centerArtifact" placeholder="请选择中心文物">
                <el-option label="青花瓷" value="青花瓷"></el-option>
                <el-option label="青铜器" value="青铜器"></el-option>
                <el-option label="书画" value="书画"></el-option>
                <el-option label="玉器" value="玉器"></el-option>
                <el-option label="陶瓷" value="陶瓷"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="6">
            <el-form-item label="关系深度">
              <el-slider 
                v-model="depth" 
                :min="1" 
                :max="5" 
                show-input 
                input-size="small"
              ></el-slider>
            </el-form-item>
          </el-col>
          
          <el-col :span="6">
            <el-form-item label="布局方式">
              <el-select v-model="layout" placeholder="请选择布局方式">
                <el-option label="力导向布局" value="force"></el-option>
                <el-option label="圆形布局" value="circular"></el-option>
                <el-option label="层次布局" value="hierarchical"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="refreshGraph">
                <i class="el-icon-refresh"></i> 刷新图谱
              </el-button>
              <el-button @click="exportGraph" style="margin-left: 10px;">
                <i class="el-icon-download"></i> 导出图谱
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 知识图谱可视化卡片 -->
    <el-card class="graph-card" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3><i class="el-icon-share"></i> 知识图谱可视化</h3>
          <div class="graph-stats">
            <span class="stat-item">
              <i class="el-icon-guide"></i> 节点: {{ nodeCount }}
            </span>
            <span class="stat-item" style="margin-left: 15px;">
              <i class="el-icon connection"></i> 关系: {{ relationCount }}
            </span>
          </div>
        </div>
      </template>
      
      <div class="graph-container">
        <div class="graph-placeholder" v-if="!graphLoaded">
          <div class="loading">
            <i class="el-icon-loading"></i>
            <p>正在加载知识图谱...</p>
          </div>
        </div>
        <div class="graph-content" v-else>
          <div class="graph-info-overlay">
            <p>以"{{ centerArtifact }}"为中心的{{ depth }}层关系网络</p>
          </div>
          <div class="graph-visualization">
            <!-- 这里应该是实际的图谱可视化组件 -->
            <div class="graph-nodes">
              <div class="node center-node">{{ centerArtifact }}</div>
              <div class="node related-node" v-for="node in relatedNodes" :key="node.id">
                {{ node.name }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 图例说明卡片 -->
    <el-card class="legend-card" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3><i class="el-icon-help"></i> 图例说明</h3>
        </div>
      </template>
      
      <div class="legend-content">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="legend-item">
              <div class="legend-color center-legend"></div>
              <span>中心文物节点</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="legend-item">
              <div class="legend-color related-legend"></div>
              <span>相关知识点节点</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="legend-item">
              <div class="legend-line"></div>
              <span>知识关联关系</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 图谱信息卡片 -->
    <el-card class="info-card" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <h3><i class="el-icon-info"></i> 图谱信息</h3>
        </div>
      </template>
      
      <div class="info-content">
        <p>当前展示了以"{{ centerArtifact }}"为中心的文物知识关联网络，包含了制作工艺、历史背景、艺术特色等多个维度的关联信息。</p>
        <div class="info-stats">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-stat-item">
                <h4>节点数量</h4>
                <p>{{ nodeCount }}</p>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-stat-item">
                <h4>关系数量</h4>
                <p>{{ relationCount }}</p>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-stat-item">
                <h4>数据来源</h4>
                <p>文物数据库、专业文献、专家知识</p>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'KnowledgeGraphView',
  data() {
    return {
      centerArtifact: '青花瓷',
      depth: 3,
      layout: 'force',
      graphLoaded: false,
      relatedNodes: [
        { id: 1, name: '制作工艺' },
        { id: 2, name: '历史背景' },
        { id: 3, name: '艺术特色' },
        { id: 4, name: '文化价值' },
        { id: 5, name: '相关人物' },
        { id: 6, name: '地理分布' },
        { id: 7, name: '材料构成' },
        { id: 8, name: '保存状况' }
      ]
    }
  },
  computed: {
    nodeCount() {
      return this.relatedNodes.length + 1 // +1 for center node
    },
    relationCount() {
      return this.relatedNodes.length
    }
  },
  mounted() {
    // 模拟图谱加载
    setTimeout(() => {
      this.graphLoaded = true
    }, 1500)
  },
  methods: {
    refreshGraph() {
      this.graphLoaded = false
      // 模拟刷新图谱
      setTimeout(() => {
        this.graphLoaded = true
        alert(`已刷新以"${this.centerArtifact}"为中心的${this.depth}层关系图谱`)
      }, 1000)
    },
    exportGraph() {
      alert('图谱导出功能已触发')
    }
  }
}
</script>

<style scoped>
.knowledge-graph-view {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 18px;
  color: #303133;
}

.card-header i {
  margin-right: 8px;
}

.graph-stats {
  display: flex;
  align-items: center;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #606266;
}

.graph-container {
  height: 500px;
  position: relative;
}

.graph-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  background: #f5f7fa;
}

.loading {
  text-align: center;
  color: #909399;
}

.loading i {
  font-size: 2rem;
  margin-bottom: 1rem;
}

.graph-content {
  height: 100%;
  position: relative;
}

.graph-info-overlay {
  position: absolute;
  top: 1rem;
  left: 1rem;
  background: rgba(255, 255, 255, 0.9);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  z-index: 10;
  font-size: 0.9rem;
  box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.graph-visualization {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.graph-nodes {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
  margin: 2rem;
}

.node {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 1rem;
  font-size: 0.9rem;
  font-weight: bold;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  z-index: 5;
}

.center-node {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  width: 120px;
  height: 120px;
}

.related-node {
  background: linear-gradient(135deg, #42b983 0%, #359c6d 100%);
  color: white;
}

.legend-content {
  padding: 20px 0;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 50%;
}

.center-legend {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.related-legend {
  background: linear-gradient(135deg, #42b983 0%, #359c6d 100%);
}

.legend-line {
  width: 30px;
  height: 4px;
  background: #42b983;
  border-radius: 2px;
}

.info-content p {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.info-stats {
  padding: 20px 0;
}

.info-stat-item {
  background: #f5f7fa;
  padding: 1.5rem;
  border-radius: 10px;
  text-align: center;
}

.info-stat-item h4 {
  color: #303133;
  margin: 0 0 0.5rem 0;
}

.info-stat-item p {
  margin: 0;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .graph-stats {
    justify-content: center;
  }
}
</style>