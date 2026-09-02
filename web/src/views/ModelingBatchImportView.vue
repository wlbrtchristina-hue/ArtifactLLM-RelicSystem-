<template>
  <div class="modeling-batch-import-view">
    <el-card class="form-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2><i class="el-icon-upload"></i> 批量导入</h2>
        </div>
      </template>
      
      <el-alert
        title="功能说明"
        description="通过上传TXT/JSON/CSV文档，系统将自动识别文档内容并将其添加到相应的实体类型中。每行代表一个实体，格式为：实体类型|属性1:值1|属性2:值2|..."
        type="info"
        show-icon
        closable
        style="margin-bottom: 20px;"
      />
      
      <el-form :model="form" label-width="120px">
        <el-form-item label="选择文件" prop="file" :rules="[{ required: true, message: '请上传文件', trigger: 'change' }]">
          <el-upload
            class="upload-demo"
            drag
            action=""
            :auto-upload="false"
            :on-change="handleFileChange"
            :file-list="fileList"
            :accept="'.txt,.json,.csv'"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="el-upload__tip" slot="tip">支持 TXT/JSON/CSV 文件，且不超过10MB</div>
          </el-upload>
        </el-form-item>
        
        <el-form-item label="实体类型映射">
          <el-table :data="typeMappings" style="width: 100%">
            <el-table-column prop="fileType" label="文件中的类型标识" width="200"></el-table-column>
            <el-table-column prop="systemType" label="系统实体类型">
              <template #default="scope">
                <el-select v-model="scope.row.systemType" placeholder="请选择系统实体类型">
                  <el-option label="青铜器" value="bronze"></el-option>
                  <el-option label="青花瓷" value="blueAndWhitePorcelain"></el-option>
                  <el-option label="水墨画" value="inkPainting"></el-option>
                  <el-option label="玉器" value="jade"></el-option>
                </el-select>
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="submitImport" :loading="loading">开始导入</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      
      <div v-if="importResult" class="import-result">
        <el-divider>导入结果</el-divider>
        <el-alert
          :title="`成功导入 ${importResult.successCount} 条记录，失败 ${importResult.failCount} 条`"
          :type="importResult.failCount > 0 ? 'warning' : 'success'"
          show-icon
        />
        
        <div v-if="importResult.failures.length > 0" style="margin-top: 20px;">
          <h4>失败记录详情：</h4>
          <el-table :data="importResult.failures" style="width: 100%">
            <el-table-column prop="lineNumber" label="行号" width="80"></el-table-column>
            <el-table-column prop="content" label="内容"></el-table-column>
            <el-table-column prop="reason" label="失败原因" width="200"></el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'

export default {
  name: 'ModelingBatchImportView',
  setup() {
    const form = reactive({
      file: null
    })
    
    const fileList = reactive([])
    const typeMappings = reactive([])
    const importResult = reactive({
      successCount: 0,
      failCount: 0,
      failures: []
    })
    
    const loading = reactive({
      value: false
    })
    
    const handleFileChange = (file, fileList) => {
      // 检查文件类型
      const allowedTypes = ['text/plain', 'application/json', 'text/csv']
      if (!allowedTypes.includes(file.raw.type)) {
        ElMessage.error('不支持的文件类型，请上传 TXT、JSON 或 CSV 文件')
        return
      }
      
      form.file = file.raw
      // 模拟解析文件内容并生成类型映射
      typeMappings.length = 0
      typeMappings.push({ fileType: '青铜器', systemType: '' })
      typeMappings.push({ fileType: '瓷器', systemType: '' })
    }
    
    const submitImport = async () => {
      if (!form.file) {
        ElMessage.warning('请上传文件')
        return
      }
      
      loading.value = true
      
      try {
        // 模拟导入过程
        await new Promise(resolve => setTimeout(resolve, 2000))
        
        // 模拟导入结果
        importResult.successCount = 42
        importResult.failCount = 3
        importResult.failures = [
          { lineNumber: 15, content: '瓷器|名称:青花瓷瓶|年代:明代|...', reason: '缺少必要属性' },
          { lineNumber: 28, content: '玉器|名称:和田玉佩|年代:汉代|...', reason: '实体类型未映射' },
          { lineNumber: 45, content: '青铜器|名称:鼎|年代:商代|...', reason: '属性格式错误' }
        ]
        
        ElMessage.success('批量导入完成')
      } catch (err) {
        ElMessage.error('导入失败：' + (err.response?.data?.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
    
    const resetForm = () => {
      form.file = null
      fileList.length = 0
      typeMappings.length = 0
      importResult.successCount = 0
      importResult.failCount = 0
      importResult.failures = []
    }
    
    return {
      form,
      fileList,
      typeMappings,
      importResult,
      loading: loading.value,
      handleFileChange,
      submitImport,
      resetForm
    }
  }
}
</script>

<style scoped>
.modeling-batch-import-view {
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

.import-result {
  margin-top: 30px;
}
</style>