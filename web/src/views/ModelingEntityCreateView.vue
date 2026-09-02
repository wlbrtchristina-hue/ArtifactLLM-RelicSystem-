<template>
  <div class="modeling-entity-create-view">
    <el-card class="form-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <h2><el-icon><Plus /></el-icon> 手动添加实体</h2>
        </div>
      </template>
      
      <el-form :model="form" label-width="120px" @submit.prevent="submitForm">
        <el-form-item label="模型选择" prop="modelId" :rules="[{ required: true, message: '请选择所属模型', trigger: 'change' }]">
          <el-select v-model="form.modelId" placeholder="请选择模型" @change="onModelChange">
            <el-option 
              v-for="model in modelList" 
              :key="model.id" 
              :label="model.name" 
              :value="model.id"
            ></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="实体类型" prop="entityDefId" :rules="[{ required: true, message: '请选择实体类型', trigger: 'change' }]">
          <el-select v-model="form.entityDefId" placeholder="请选择实体类型" @change="onEntityDefChange" :disabled="!form.modelId">
            <el-option 
              v-for="def in entityDefs" 
              :key="def.id" 
              :label="def.name" 
              :value="def.id"
            ></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="实体名称" prop="name" :rules="[{ required: true, message: '请输入实体名称', trigger: 'blur' }]">
          <el-input v-model="form.name" placeholder="请输入实体名称（用于显示）"></el-input>
        </el-form-item>
        
        <el-divider v-if="currentAttributes.length > 0">动态属性</el-divider>
        
        <div class="attributes-section" v-if="currentAttributes.length > 0">
          <el-row :gutter="20">
            <el-col :span="12" v-for="attr in currentAttributes" :key="attr.code">
              <el-form-item 
                :label="attr.name" 
                :prop="`data.${attr.code}`"
                :rules="attr.required ? [{ required: true, message: `请输入${attr.name}`, trigger: 'blur' }] : []"
              >
                <!-- 文本输入 -->
                <el-input 
                  v-if="attr.type === 'text'" 
                  v-model="form.data[attr.code]" 
                  :placeholder="attr.description || `请输入${attr.name}`"
                ></el-input>
                
                <!-- 数字输入 -->
                <el-input-number 
                  v-else-if="attr.type === 'number'" 
                  v-model="form.data[attr.code]"
                  style="width: 100%"
                ></el-input-number>

                <!-- 日期选择 -->
                <el-date-picker
                  v-else-if="attr.type === 'date'"
                  v-model="form.data[attr.code]"
                  type="date"
                  placeholder="选择日期"
                  style="width: 100%"
                  value-format="YYYY-MM-DD"
                ></el-date-picker>

                <!-- 布尔值 -->
                <el-switch
                  v-else-if="attr.type === 'boolean'"
                  v-model="form.data[attr.code]"
                ></el-switch>

                <!-- 下拉选择 -->
                <el-select
                  v-else-if="attr.type === 'select'"
                  v-model="form.data[attr.code]"
                  placeholder="请选择"
                  style="width: 100%"
                >
                  <el-option
                    v-for="opt in getOptions(attr.options)"
                    :key="opt"
                    :label="opt"
                    :value="opt"
                  ></el-option>
                </el-select>

                <!-- 长文本 -->
                <el-input
                  v-else-if="attr.type === 'textarea'"
                  v-model="form.data[attr.code]"
                  type="textarea"
                  :rows="3"
                ></el-input>

                <!-- 默认输入 -->
                <el-input v-else v-model="form.data[attr.code]"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>
        
        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            {{ isAdminOrAdvanced ? '保存实体' : '提交审核' }}
          </el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { reactive, ref, computed, onMounted, watchEffect } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { listModels, getModelDef, saveInstance } from '@/api/modeling'

export default {
  name: 'ModelingEntityCreateView',
  components: { Plus },
  setup() {
    const store = useStore()
    
    const form = reactive({
      modelId: '',
      entityDefId: '',
      name: '',
      data: {}
    })
    
    const loading = ref(false)
    const modelList = ref([])
    const entityDefs = ref([]) // 当前选中模型的实体定义列表
    const currentAttributes = ref([]) // 当前选中实体的属性定义列表
    
    // 获取用户角色
    const userRole = computed(() => store.state.user?.role || 'user')
    const isAdminOrAdvanced = computed(() => 
      userRole.value === 'admin' || userRole.value === 'advanced'
    )

    // 初始化加载模型列表
    const fetchModels = async () => {
      try {
        const res = await listModels()
        modelList.value = res.data || []
      } catch (err) {
        console.error(err)
        ElMessage.error('获取模型列表失败')
      }
    }

    // 模型改变时，加载该模型的实体定义
    const onModelChange = async (modelId) => {
      form.entityDefId = ''
      form.name = ''
      form.data = {}
      entityDefs.value = []
      currentAttributes.value = []
      
      if (!modelId) return

      try {
        const res = await getModelDef(modelId)
        if (res.data && res.data.entities) {
          // 这里假设后端返回的结构包含 entities 列表
          // 如果 getModelDef 返回的是完整 DTO，结构应该是 res.data.entities
          entityDefs.value = res.data.entities
        }
      } catch (err) {
        console.error(err)
        ElMessage.error('获取模型定义失败')
      }
    }

    // 实体类型改变时，加载对应的属性定义并生成表单
    const onEntityDefChange = (entityDefId) => {
      form.data = {}
      currentAttributes.value = []
      
      const selectedDef = entityDefs.value.find(d => d.id === entityDefId)
      if (selectedDef && selectedDef.attributes) {
        currentAttributes.value = selectedDef.attributes
        // 初始化表单数据
        selectedDef.attributes.forEach(attr => {
           // 根据类型设置默认值
           if (attr.type === 'boolean') {
             form.data[attr.code] = false
           } else {
             form.data[attr.code] = null
           }
        })
        
        // 如果有名称属性，初始化时同步实体名称
        if (form.data.name === null && form.name) {
          form.data.name = form.name
        }
      }
    }
    
    // 实现实体名称和动态属性中的名称字段双向同步
    watchEffect(() => {
      // 当实体名称变化时，同步到动态属性的名称字段
      if (form.data && form.data.name !== form.name) {
        form.data.name = form.name
      }
    })
    
    watchEffect(() => {
      // 当动态属性的名称字段变化时，同步到实体名称
      if (form.data && form.data.name && form.data.name !== form.name) {
        form.name = form.data.name
      }
    })

    const getOptions = (optionsStr) => {
      try {
        return JSON.parse(optionsStr)
      } catch (e) {
        return []
      }
    }
    
    const submitForm = async () => {
      if (!form.modelId || !form.entityDefId || !form.name) {
        ElMessage.warning('请填写必填项')
        return
      }
      
      loading.value = true
      
      try {
        const submitData = {
          modelId: form.modelId,
          entityDefId: form.entityDefId,
          name: form.name,
          data: form.data
        }

        await saveInstance(submitData)
        
        if (isAdminOrAdvanced.value) {
          ElMessage.success('实体保存成功')
        } else {
          ElMessage.success('实体已提交审核')
        }
        resetForm()
      } catch (err) {
        console.error(err)
        ElMessage.error('操作失败：' + (err?.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }
    
    const resetForm = () => {
      form.modelId = ''
      form.entityDefId = ''
      form.name = ''
      form.data = {}
      entityDefs.value = []
      currentAttributes.value = []
    }

    onMounted(() => {
      fetchModels()
    })
    
    return {
      form,
      loading,
      isAdminOrAdvanced,
      modelList,
      entityDefs,
      currentAttributes,
      onModelChange,
      onEntityDefChange,
      getOptions,
      submitForm,
      resetForm
    }
  }
}
</script>

<style scoped>
.modeling-entity-create-view {
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

.card-header h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 18px;
}

.attributes-section {
  margin-bottom: 20px;
}
</style>