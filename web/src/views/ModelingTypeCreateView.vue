<template>
  <div class="modeling-type-create-view">
    <!-- 用户引导对话框 -->
    <el-dialog
      v-model="showGuideDialog"
      title="使用引导"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-steps :active="guideStep" finish-status="success" align-center>
        <el-step title="组件库" description="了解组件库功能"></el-step>
        <el-step title="画布操作" description="学习画布操作"></el-step>
        <el-step title="配置面板" description="了解配置功能"></el-step>
      </el-steps>
      
      <div class="guide-content">
        <div v-if="guideStep === 0" class="guide-step-content">
          <h3>组件库介绍</h3>
          <ul>
            <li><strong>属性组件库：</strong>包含基础组件和自定义属性，可以拖拽到实体类型上添加属性</li>
            <li><strong>关系组件库：</strong>包含各种关系类型，点击选择后可通过实体类型的连接点创建关系</li>
            <li><strong>已有实体类型库：</strong>显示已创建的实体类型，可以直接拖拽到画布使用</li>
          </ul>
          <p><strong>提示：</strong>点击左侧的展开/收起按钮可以控制组件库的显示</p>
        </div>
        
        <div v-if="guideStep === 1" class="guide-step-content">
          <h3>画布操作</h3>
          <ul>
            <li><strong>添加实体类型：</strong>点击右下角"添加实体类型"按钮，或从已有实体类型库拖拽</li>
            <li><strong>移动实体类型：</strong>直接拖拽实体类型即可移动位置</li>
            <li><strong>创建关系类型：</strong>先选择关系类型，然后点击源实体类型的连接点，拖拽到目标实体类型的连接点（即点对点有向性连接）</li>
            <li><strong>配置实体类型：</strong>点击实体类型，在右侧配置面板中修改属性</li>
          </ul>
        </div>
        
        <div v-if="guideStep === 2" class="guide-step-content">
          <h3>配置面板</h3>
          <ul>
            <li><strong>实体类型配置：</strong>可以修改实体名称、描述和属性列表</li>
            <li><strong>属性配置：</strong>可以设置属性类型、是否必填、选项等</li>
            <li><strong>关系配置：</strong>可以修改关系名称、类型和描述</li>
            <li><strong>保存模型：</strong>在底部输入模型名称，点击"保存模型"或"提交审核"</li>
          </ul>
        </div>
      </div>
      
      <template #footer>
        <div class="guide-footer">
          <el-button @click="prevGuideStep" :disabled="guideStep === 0">上一步</el-button>
          <el-button v-if="guideStep < 2" type="primary" @click="nextGuideStep">下一步</el-button>
          <el-button v-else type="primary" @click="finishGuide">完成</el-button>
        </div>
      </template>
    </el-dialog>
    
    <!-- 帮助按钮 -->
    <el-button 
      class="help-button"
      type="primary"
      circle
      size="large"
      @click="startGuide"
      title="查看使用引导"
    >
      <el-icon><QuestionFilled /></el-icon>
    </el-button>
    
    <!-- 主容器 -->
    <div class="main-container">
      <!-- 左侧组件库 -->
      <div class="left-panel-wrapper" :class="{ 'collapsed': !isLeftPanelExpanded, 'fullscreen-overlay': isCanvasExpanded }">
        <!-- 侧边栏Tab -->
        <div class="side-tab-bar" v-show="isLeftPanelExpanded">
            <div class="tab-item" :class="{ active: activeSidebarTab === 'attribute' }" @click="activeSidebarTab = 'attribute'" title="属性组件库">
                <el-icon><Menu /></el-icon>
                <span>属性</span>
            </div>
            <div class="tab-item" :class="{ active: activeSidebarTab === 'relation' }" @click="activeSidebarTab = 'relation'" title="关系组件库">
                <el-icon><Link /></el-icon>
                <span>关系</span>
            </div>
            <div class="tab-item" :class="{ active: activeSidebarTab === 'entity' }" @click="activeSidebarTab = 'entity'" title="已有实体类型">
                <el-icon><Collection /></el-icon>
                <span>模型</span>
            </div>
        </div>
        
        <!-- 内容区域 -->
        <div class="side-content-area" v-show="isLeftPanelExpanded">
             <!-- 属性组件库 -->
             <div v-show="activeSidebarTab === 'attribute'" class="tab-panel-content">
                 <div class="panel-header-inline">
                     <h3>属性组件库</h3>
                 </div>
                 <el-scrollbar>
                   <div class="component-section">
                     <h4 class="section-title">基础组件(必选项)</h4>
                     <div class="component-item basic-item" v-for="(value, key) in basicComponent" :key="key">
                       <el-input :value="value" :placeholder="key" disabled size="small" />
                     </div>
                   </div>

                   <div class="component-section">
                     <h4 class="section-title">专属组件(可拖拽选择)</h4>
                     <div
                       v-for="(component, index) in attributeComponents"
                       :key="`attr-${index}`"
                       class="component-item draggable-item"
                       draggable="true"
                       @dragstart="handleDragStart($event, 'attribute', component)"
                       @dragover="handleDragOver"
                     >
                       <el-input v-model="component.name" placeholder="属性名称" disabled size="small" />
                       <el-icon class="drag-icon"><Link /></el-icon>
                     </div>
                     <el-input
                       v-model="newAttributeName"
                       placeholder="输入属性名称"
                       @keyup.enter="addCustomAttribute"
                       size="small"
                       class="add-input"
                     >
                       <template #append>
                         <el-button @click="addCustomAttribute" size="small" type="primary">添加</el-button>
                       </template>
                     </el-input>
                   </div>
                 </el-scrollbar>
             </div>
             
             <!-- 关系组件库 -->
             <div v-show="activeSidebarTab === 'relation'" class="tab-panel-content">
                 <div class="panel-header-inline">
                     <h3>关系组件库</h3>
                 </div>
                 <el-scrollbar>
                   <div class="component-section">
                     <div
                       v-for="(relation, index) in relationComponents"
                       :key="`rel-${index}`"
                       class="component-item relation-item"
                       :class="{
                         'selected-relation': selectedRelationType?.id === relation.id,
                         'relation-linked': relationUsageMap[relation.id] > 0
                       }"
                       @click="selectRelationType(relation)"
                       @dblclick="startQuickCreateRelation(relation)"
                     >
                       <el-input v-model="relation.name" placeholder="关系名称" disabled size="small" />
                       <div class="relation-actions">
                         <el-tag
                           size="mini"
                           type="info"
                           class="relation-usage"
                           :effect="relationUsageMap[relation.id] > 0 ? 'dark' : 'plain'"
                         >
                           {{ relationUsageMap[relation.id] || 0 }} 条
                         </el-tag>
                         <el-icon class="relation-icon" title="点击选择，双击快速创建"><Link /></el-icon>
                         <el-tag v-if="selectedRelationType?.id === relation.id" size="mini" type="success">已选</el-tag>
                       </div>
                     </div>
                     <el-input
                       v-model="newRelationName"
                       placeholder="输入关系名称"
                       @keyup.enter="addCustomRelation"
                       size="small"
                       class="add-input"
                     >
                       <template #append>
                         <el-button @click="addCustomRelation" size="small" type="primary">添加</el-button>
                       </template>
                     </el-input>
                   </div>
                 </el-scrollbar>
             </div>
             
             <!-- 实体组件库 -->
             <div v-show="activeSidebarTab === 'entity'" class="tab-panel-content">
                  <div class="panel-header-inline">
                    <h3>已有模型库</h3>
                    <el-button 
                      text 
                      size="small" 
                      @click="loadExistingEntityTypes"
                      :loading="loadingEntityTypes"
                      title="刷新列表"
                    >
                      <el-icon><Refresh /></el-icon>
                    </el-button>
                  </div>
                 <el-scrollbar>
                   <div class="component-section">
                     <div v-if="existingEntityTypes.length === 0 && !loadingEntityTypes" class="empty-tip">
                       <el-empty description="暂无已有实体类型" :image-size="60" />
                     </div>
                     <div
                       v-for="(entityType, index) in existingEntityTypes"
                       :key="`entity-type-${index}`"
                       class="component-item entity-type-item draggable-item"
                       draggable="true"
                       @dragstart="handleDragStart($event, 'entityType', entityType)"
                       @dragover="handleDragOver"
                     >
                       <div class="entity-type-info">
                         <el-text class="entity-type-name" :title="entityType.description">{{ entityType.name }}</el-text>
                         <el-text v-if="entityType.description" size="small" type="info" class="entity-type-desc">
                           {{ entityType.description }}
                         </el-text>
                       </div>
                       <el-icon class="drag-icon"><Link /></el-icon>
                     </div>
                   </div>
                 </el-scrollbar>
             </div>
        </div>

        <!-- 展开/收起按钮 -->
        <div class="panel-toggle-wrapper">
          <el-button 
            :type="isLeftPanelExpanded ? 'primary' : 'default'"
            @click="toggleLeftPanel"
            class="panel-toggle-btn"
            circle
            size="small"
            :title="isLeftPanelExpanded ? '收起组件库' : '展开组件库'"
          >
            <el-icon :class="{ 'arrow-left-black': isLeftPanelExpanded }">
              <ArrowLeft v-if="isLeftPanelExpanded" />
              <ArrowRight v-else />
            </el-icon>
          </el-button>
          <span class="toggle-label" v-if="!isLeftPanelExpanded">展开</span>
        </div>
      </div>

      <!-- 中央画布区域 -->
      <div class="center-panel" :class="{ 'panel-expanded': isCanvasExpanded, 'panel-left-collapsed': !isLeftPanelExpanded }">
        <el-card class="canvas-card" :class="{ 'card-expanded': isCanvasExpanded }">
          <template #header>
            <div class="panel-header">
              <el-icon class="panel-icon"><Menu /></el-icon>
              <h3>新增实体类型-画布</h3>
              <div class="canvas-header-actions">
                <el-button type="primary" size="small" @click="addNewEntity" icon="Plus" class="header-add-btn">添加实体</el-button>
                <div class="divider"></div>
                <el-button-group size="small" style="margin-right: 10px">
                  <el-button @click="zoomOut" title="缩小"><el-icon><ZoomOut /></el-icon></el-button>
                  <el-button style="width: 60px">{{ Math.round(canvasScale * 100) }}%</el-button>
                  <el-button @click="zoomIn" title="放大"><el-icon><ZoomIn /></el-icon></el-button>
                  <el-button @click="resetZoom" title="重置"><el-icon><Refresh /></el-icon></el-button>
                </el-button-group>
                <el-button size="small" type="text" @click="toggleCanvasSize" class="resize-btn">
                  <el-icon><FullScreen v-if="!isCanvasExpanded" /><Aim v-else /></el-icon>
                  {{ isCanvasExpanded ? '还原' : '全屏' }}
                </el-button>
                <el-button size="small" type="text" @click="clearCanvas" class="clear-btn">清空画布</el-button>
              </div>
            </div>
          </template>

          <div
            class="canvas-area"
            :class="{ 'canvas-expanded': isCanvasExpanded }"
            @dragover.prevent
            @drop="handleDrop"
            @click="handleCanvasClick"
            @wheel="handleWheelZoom"
            ref="canvasRef"
          >
            <div class="canvas-scaler" :style="{ transform: `scale(${canvasScale})` }">
            <!-- 实体和关系将在这里动态生成 -->
            <div
              v-for="entity in canvasEntities"
              :key="entity.id"
              class="entity-node"
              :style="{ left: entity.x + 'px', top: entity.y + 'px' }"
              @mousedown="handleEntityMouseDown($event, entity)"
              @click="selectEntity(entity)"
              :class="{ 'selected': selectedElement?.id === entity.id && selectedElementType === 'entity' }"
            >
              <!-- 连接点 - 优化版本，确保绝对可点击 -->
              <div
                class="connection-point top"
                @mousedown.stop="startCreateRelation($event, entity, 'top')"
                @click.stop
                @mouseenter="highlightConnectionPoint($event)"
                @mouseleave="unhighlightConnectionPoint($event)"
              ></div>
              <div
                class="connection-point right"
                @mousedown.stop="startCreateRelation($event, entity, 'right')"
                @click.stop
                @mouseenter="highlightConnectionPoint($event)"
                @mouseleave="unhighlightConnectionPoint($event)"
              ></div>
              <div
                class="connection-point bottom"
                @mousedown.stop="startCreateRelation($event, entity, 'bottom')"
                @click.stop
                @mouseenter="highlightConnectionPoint($event)"
                @mouseleave="unhighlightConnectionPoint($event)"
              ></div>
              <div
                class="connection-point left"
                @mousedown.stop="startCreateRelation($event, entity, 'left')"
                @click.stop
                @mouseenter="highlightConnectionPoint($event)"
                @mouseleave="unhighlightConnectionPoint($event)"
              ></div>

              <div class="entity-header">
                <span class="entity-name">{{ entity.name }}</span>
                <el-tag size="small" type="info">实体类</el-tag>
              </div>
              <div class="entity-attributes">
                <div
                  v-for="attr in entity.attributes"
                  :key="`${entity.id}-attr-${attr.id}`"
                  class="attribute-item"
                  @click.stop="selectAttribute(attr, entity)"
                >
                  <el-icon class="attr-icon"><InfoFilled /></el-icon>
                  {{ attr.name }}
                </div>
              </div>
              <div class="entity-footer">
                <el-text size="small" type="secondary">拖拽移动 | 点击配置</el-text>
              </div>
            </div>

            <!-- 关系连线 - 放在实体下方 -->
            <svg class="relation-lines" width="100%" height="100%" style="position: absolute; top: 0; left: 0; z-index: 1; pointer-events: none;">
              <defs>
                <marker
                  id="arrowhead"
                  markerWidth="10"
                  markerHeight="7"
                  refX="9"
                  refY="3.5"
                  orient="auto"
                >
                  <polygon points="0 0, 10 3.5, 0 7" fill="#409EFF" />
                </marker>
                <marker
                  id="arrowhead-selected"
                  markerWidth="12"
                  markerHeight="8"
                  refX="10"
                  refY="4"
                  orient="auto"
                >
                  <polygon points="0 0, 12 4, 0 8" fill="#67C23A" />
                </marker>
              </defs>
              <!-- 现有关系 -->
              <g
                v-for="relation in canvasRelations"
                :key="relation.id"
                :class="{ 'selected': selectedElement?.id === relation.id && selectedElementType === 'relation' }"
              >
                <line
                  :x1="relation.source.x"
                  :y1="relation.source.y"
                  :x2="relation.target.x"
                  :y2="relation.target.y"
                  :stroke="getRelationStrokeColor(relation)"
                  :stroke-width="getRelationStrokeWidth(relation)"
                  :marker-end="selectedElement?.id === relation.id && selectedElementType === 'relation' ? 'url(#arrowhead-selected)' : 'url(#arrowhead)'"
                  @click="selectRelation(relation)"
                  style="cursor: pointer;"
                />
                <text
                  :x="(relation.source.x + relation.target.x) / 2"
                  :y="(relation.source.y + relation.target.y) / 2 - 10"
                  :fill="getRelationTextColor(relation)"
                  text-anchor="middle"
                  font-size="12"
                  font-weight="500"
                  style="pointer-events: all; cursor: pointer; background: rgba(255,255,255,0.9); padding: 2px 6px; border-radius: 8px; border: 1px solid rgba(64,158,255,0.3);"
                  @click="selectRelation(relation)"
                >
                  {{ relation.name }}
                </text>
              </g>
              <!-- 正在创建的关系 -->
              <line
                v-if="creatingRelation"
                :x1="creatingRelation.startX"
                :y1="creatingRelation.startY"
                :x2="creatingRelation.currentX"
                :y2="creatingRelation.currentY"
                stroke="#67C23A"
                stroke-width="2.5"
                stroke-dasharray="5,5"
                opacity="0.8"
              />
            </svg>

            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧属性配置面板 -->
      <div class="right-panel" :class="{ 'panel-collapsed': isCanvasExpanded }">
        <el-card class="panel-card config-card">
          <template #header>
            <div class="panel-header">
              <el-icon class="panel-icon"><Setting /></el-icon>
              <h3>属性配置</h3>
            </div>
          </template>

          <div class="config-content">
          <div v-if="selectedElement && selectedElementType === 'entity'" class="config-section">
            <h4 class="config-title">{{ selectedElement.name }} 实体类型配置</h4>
            <el-form :model="selectedElementConfig" label-width="80px" size="small">
              <el-form-item label="名称">
                <el-input v-model="selectedElementConfig.name" placeholder="名称" />
              </el-form-item>
              <el-form-item label="描述">
                <el-input
                  v-model="selectedElementConfig.description"
                  type="textarea"
                  placeholder="输入实体类型描述"
                  :rows="3"
                />
              </el-form-item>
              <el-form-item label="属性列表">
                <div class="attribute-list">
                  <div
                    v-for="(attr, index) in selectedElement.attributes"
                    :key="`${selectedElement.id}-attr-${attr.id}`"
                    class="attribute-item-config"
                  >
                    <div class="attribute-info">
                      <span class="attribute-name">{{ attr.name }}</span>
                      <el-tag size="mini">{{ getTypeName(attr.type || 'text') }}</el-tag>
                    </div>
                    <el-button
                      type="danger"
                      size="small"
                      circle
                      @click="deleteAttribute(selectedElement, index)"
                    >
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
                </div>
              </el-form-item>
            </el-form>
            <div class="config-actions">
              <el-button type="primary" class="save-btn" @click="saveEntityConfig" icon="Check">保存配置</el-button>
              <el-button type="danger" circle @click="deleteSelectedElement">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>

          <div v-else-if="selectedElement && selectedElementType === 'attribute'" class="config-section">
            <h4 class="config-title">{{ selectedElement.name }} 属性配置</h4>
            <el-form :model="selectedElementConfig" label-width="80px" size="small">
              <el-form-item label="字段名称">
                <el-input v-model="selectedElementConfig.name" placeholder="请输入属性名称" />
              </el-form-item>
              <el-form-item label="字段类型">
                <el-select v-model="selectedElementConfig.type" placeholder="选择字段类型">
                  <el-option label="文本" value="text"></el-option>
                  <el-option label="数字" value="number"></el-option>
                  <el-option label="日期" value="date"></el-option>
                  <el-option label="布尔值" value="boolean"></el-option>
                  <el-option label="长文本" value="textarea"></el-option>
                  <el-option label="下拉选择" value="select"></el-option>
                  <el-option label="单选按钮" value="radio"></el-option>
                  <el-option label="复选框" value="checkbox"></el-option>
                  <el-option label="文件上传" value="file"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="是否必填">
                <el-checkbox v-model="selectedElementConfig.required"></el-checkbox>
              </el-form-item>
              <el-form-item label="备注说明">
                <el-input
                  v-model="selectedElementConfig.description"
                  type="textarea"
                  placeholder="输入备注信息"
                  :rows="3"
                />
              </el-form-item>

              <div v-if="['select', 'radio', 'checkbox'].includes(selectedElementConfig.type)" class="options-section">
                <el-form-item label="选项设置">
                  <div
                    v-for="(option, index) in selectedElementConfig.options || []"
                    :key="index"
                    class="option-item"
                  >
                    <el-input
                      v-model="selectedElementConfig.options[index]"
                      placeholder="选项值"
                      style="width: 200px; margin-right: 8px;"
                    ></el-input>
                    <el-button
                      type="danger"
                      size="small"
                      icon="Delete"
                      @click="deleteOption(selectedElementConfig, index)"
                    ></el-button>
                  </div>
                  <el-button
                    type="primary"
                    size="small"
                    icon="Plus"
                    @click="addOption(selectedElementConfig)"
                    style="margin-top: 8px;"
                  >
                    添加选项
                  </el-button>
                </el-form-item>
              </div>
            </el-form>
            <div class="config-actions">
              <el-button type="primary" class="save-btn" @click="saveAttributeConfig" icon="Check">保存配置</el-button>
              <el-button type="danger" circle @click="deleteSelectedElement">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>

          <div v-else-if="selectedElement && selectedElementType === 'relation'" class="config-section">
            <h4 class="config-title">{{ selectedElement.name }} 关系配置</h4>
            <el-form :model="selectedElementConfig" label-width="80px" size="small">
              <el-form-item label="关系名称">
                <el-input v-model="selectedElementConfig.name" placeholder="请输入关系名称" />
              </el-form-item>
              <el-form-item label="源实体">
                <el-input
                  :value="getEntityNameById(selectedElement.sourceId)"
                  disabled
                  placeholder="源实体"
                />
              </el-form-item>
              <el-form-item label="目标实体">
                <el-input
                  :value="getEntityNameById(selectedElement.targetId)"
                  disabled
                  placeholder="目标实体"
                />
              </el-form-item>
              <el-form-item label="关系类型">
                <el-select v-model="selectedElementConfig.relationType" placeholder="选择关系类型">
                  <el-option label="一对一" value="one-to-one"></el-option>
                  <el-option label="一对多" value="one-to-many"></el-option>
                  <el-option label="多对一" value="many-to-one"></el-option>
                  <el-option label="多对多" value="many-to-many"></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="关系描述">
                <el-input
                  v-model="selectedElementConfig.description"
                  type="textarea"
                  placeholder="输入关系描述"
                  :rows="3"
                />
              </el-form-item>
            </el-form>
            <div class="config-actions">
              <el-button type="primary" class="save-btn" @click="saveRelationConfig" icon="Check">保存配置</el-button>
              <el-button type="danger" circle @click="deleteSelectedElement">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>

          <div v-else class="config-section empty-config">
            <el-empty description="请选择画布中的元素进行配置" :image-size="100" />
          </div>
          </div>
        </el-card>

        <!-- 操作按钮 -->
        <div class="action-buttons" :class="{ 'buttons-collapsed': isCanvasExpanded }">
          <div class="model-name-wrapper">
            <span class="model-name-label">模型名字：</span>
            <el-input
              v-model="modelName"
              placeholder="请输入模型名称"
              class="model-name-input"
              size="large"
              prefix-icon="Document"
            />
          </div>
          <el-button type="primary" @click="saveModel" size="large" icon="Download">保存模型</el-button>
        </div>
      </div>
    </div>

    <!-- 全屏状态下的属性配置浮窗 -->
    <div
      v-if="isCanvasExpanded && showFloatingPanel"
      class="floating-config-panel"
      :style="{ left: floatingPanelPosition.x + 'px', top: floatingPanelPosition.y + 'px' }"
    >
      <div class="floating-panel-header" @mousedown="startDragFloatingPanel">
        <div class="panel-title">
          <el-icon><Setting /></el-icon>
          <span>属性配置</span>
        </div>
        <div class="close-btn-wrapper" @click.stop="showFloatingPanel = false">
          <div class="close-btn-inner">
            <span class="close-icon">×</span>
          </div>
        </div>
      </div>
      <div class="floating-panel-content">
        <div v-if="selectedElement && selectedElementType === 'entity'" class="config-section">
          <h4 class="config-title">{{ selectedElement.name }} 实体类型配置</h4>
          <el-form :model="selectedElementConfig" label-width="80px" size="small">
            <el-form-item label="名称">
              <el-input v-model="selectedElementConfig.name" placeholder="名称" />
            </el-form-item>
            <el-form-item label="描述">
              <el-input
                v-model="selectedElementConfig.description"
                type="textarea"
                placeholder="输入实体类型描述"
                :rows="3"
              />
            </el-form-item>
            <el-form-item label="属性列表">
              <div class="attribute-list">
                <div
                  v-for="(attr, index) in selectedElement.attributes"
                  :key="`${selectedElement.id}-attr-${attr.id}`"
                  class="attribute-item-config"
                >
                  <div class="attribute-info">
                    <span class="attribute-name">{{ attr.name }}</span>
                    <el-tag size="mini">{{ getTypeName(attr.type || 'text') }}</el-tag>
                  </div>
                  <el-button
                    type="danger"
                    size="small"
                    circle
                    @click="deleteAttribute(selectedElement, index)"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </el-form-item>
          </el-form>
          <div class="config-actions">
            <el-button type="primary" class="save-btn" @click="saveEntityConfig" icon="Check">保存配置</el-button>
            <el-button type="danger" circle @click="deleteSelectedElement">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>

        <div v-else-if="selectedElement && selectedElementType === 'attribute'" class="config-section">
          <h4 class="config-title">{{ selectedElement.name }} 属性配置</h4>
          <el-form :model="selectedElementConfig" label-width="80px" size="small">
            <el-form-item label="字段名称">
              <el-input v-model="selectedElementConfig.name" placeholder="请输入属性名称" />
            </el-form-item>
            <el-form-item label="字段类型">
              <el-select v-model="selectedElementConfig.type" placeholder="选择字段类型">
                <el-option label="文本" value="text"></el-option>
                <el-option label="数字" value="number"></el-option>
                <el-option label="日期" value="date"></el-option>
                <el-option label="布尔值" value="boolean"></el-option>
                <el-option label="长文本" value="textarea"></el-option>
                <el-option label="下拉选择" value="select"></el-option>
                <el-option label="单选按钮" value="radio"></el-option>
                <el-option label="复选框" value="checkbox"></el-option>
                <el-option label="文件上传" value="file"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="是否必填">
              <el-checkbox v-model="selectedElementConfig.required"></el-checkbox>
            </el-form-item>
            <el-form-item label="备注说明">
              <el-input
                v-model="selectedElementConfig.description"
                type="textarea"
                placeholder="输入备注信息"
                :rows="3"
              />
            </el-form-item>

            <div v-if="['select', 'radio', 'checkbox'].includes(selectedElementConfig.type)" class="options-section">
              <el-form-item label="选项设置">
                <div
                  v-for="(option, index) in selectedElementConfig.options || []"
                  :key="index"
                  class="option-item"
                >
                  <el-input
                    v-model="selectedElementConfig.options[index]"
                    placeholder="选项值"
                    style="width: 200px; margin-right: 8px;"
                  ></el-input>
                  <el-button
                    type="danger"
                    size="small"
                    icon="Delete"
                    @click="deleteOption(selectedElementConfig, index)"
                  ></el-button>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  icon="Plus"
                  @click="addOption(selectedElementConfig)"
                  style="margin-top: 8px;"
                >
                  添加选项
                </el-button>
              </el-form-item>
            </div>
          </el-form>
          <div class="config-actions">
            <el-button type="primary" class="save-btn" @click="saveAttributeConfig" icon="Check">保存配置</el-button>
            <el-button type="danger" circle @click="deleteSelectedElement">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>

        <div v-else-if="selectedElement && selectedElementType === 'relation'" class="config-section">
          <h4 class="config-title">{{ selectedElement.name }} 关系配置</h4>
          <el-form :model="selectedElementConfig" label-width="80px" size="small">
            <el-form-item label="关系名称">
              <el-input v-model="selectedElementConfig.name" placeholder="请输入关系名称" />
            </el-form-item>
            <el-form-item label="源实体">
              <el-input
                :value="getEntityNameById(selectedElement.sourceId)"
                disabled
                placeholder="源实体"
              />
            </el-form-item>
            <el-form-item label="目标实体">
              <el-input
                :value="getEntityNameById(selectedElement.targetId)"
                disabled
                placeholder="目标实体"
              />
            </el-form-item>
            <el-form-item label="关系类型">
              <el-select v-model="selectedElementConfig.relationType" placeholder="选择关系类型">
                <el-option label="一对一" value="one-to-one"></el-option>
                <el-option label="一对多" value="one-to-many"></el-option>
                <el-option label="多对一" value="many-to-one"></el-option>
                <el-option label="多对多" value="many-to-many"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="关系描述">
              <el-input
                v-model="selectedElementConfig.description"
                type="textarea"
                placeholder="输入关系描述"
                :rows="3"
              />
            </el-form-item>
          </el-form>
          <div class="config-actions">
            <el-button type="primary" class="save-btn" @click="saveRelationConfig" icon="Check">保存配置</el-button>
            <el-button type="danger" circle @click="deleteSelectedElement">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>

        <div v-else class="config-section empty-config">
          <el-empty description="请选择画布中的元素进行配置" :image-size="60" />
        </div>
      </div>
    </div>

    <!-- 全屏状态下的浮窗开关按钮 -->
    <div v-if="isCanvasExpanded" class="floating-panel-toggle" @click="showFloatingPanel = true" v-show="!showFloatingPanel">
      <el-icon><Setting /></el-icon>
      <span>属性配置</span>
    </div>
  </div>
</template>

<script>
import { reactive, ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox, ElEmpty } from 'element-plus'
import { Link, Menu, Setting, InfoFilled, Plus, Check, Delete, Download, Upload, Refresh, Document, FullScreen, Aim, ArrowLeft, ArrowRight, Collection, QuestionFilled, ZoomIn, ZoomOut, Close } from '@element-plus/icons-vue'
import { saveModelDef, listModels, getModelDef } from '@/api/modeling'

export default {
  name: 'ModelingTypeCreateView',
  components: { ElEmpty, Link, Menu, Setting, InfoFilled, Plus, Check, Delete, Download, Upload, Refresh, Document, FullScreen, Aim, ArrowLeft, ArrowRight, Collection, QuestionFilled, ZoomIn, ZoomOut },
  setup() {
    const store = useStore()

    // 基础组件数据
    const basicComponent = reactive({
      name: '名称',
      age: '年代'
    })

    // 属性组件库数据
    const attributeComponents = ref([
      { id: 'attr-2', name: '类型' },
      { id: 'attr-3', name: '尺寸' },
      { id: 'attr-4', name: '出土地点' },
      { id: 'attr-5', name: '馆藏单位' }
    ])

    // 关系组件库数据
    const relationComponents = ref([
      { id: 'rel-1', name: '属于' },
      { id: 'rel-2', name: '包含' },
      { id: 'rel-3', name: '组成' },
      { id: 'rel-4', name: '关联于' },
      { id: 'rel-5', name: '出土于' },
      { id: 'rel-6', name: '修复于' },
      { id: 'rel-7', name: '捐赠于' },
      { id: 'rel-8', name: '收藏于' }
    ])

    // 新属性和关系的输入框
    const newAttributeName = ref('')
    const newRelationName = ref('')

    // 已有实体类型库数据
    const existingEntityTypes = ref([])
    const loadingEntityTypes = ref(false)
    
    // 左侧面板展开/收起状态
    const isLeftPanelExpanded = ref(true)

    // 用户引导相关
    const showGuideDialog = ref(false)
    const guideStep = ref(0)

    // 画布上的实体和关系数据
    const canvasEntities = ref([])

    const canvasRelations = ref([])

    // 模型名称
    const modelName = ref('文物实体模型')

    // 选中的元素
    const selectedElement = ref(null)
    const selectedElementType = ref(null)
    const selectedEntity = ref(null) // 用于存储属性所属的实体
    const selectedElementConfig = reactive({})

    // 画布引用
    const canvasRef = ref(null)

    // 加载状态
    const loading = ref(false)

    // 拖拽状态
    const isDragging = ref(false)
    const dragOffset = reactive({ x: 0, y: 0 })

    // 关系创建状态
    const creatingRelation = ref(null)
    
    // 选中的关系类型
    const selectedRelationType = ref(null)
    
    // 左侧关系类型与画布关系的关联统计
    const relationUsageMap = computed(() => {
      const usage = {}
      relationComponents.value.forEach(type => {
        usage[type.id] = 0
      })
      canvasRelations.value.forEach(relation => {
        if (relation?.relationTypeId) {
          usage[relation.relationTypeId] = (usage[relation.relationTypeId] || 0) + 1
        }
      })
      return usage
    })
    
    // 画布展开状态
    const isCanvasExpanded = ref(false)
    
    // 画布缩放状态
    const canvasScale = ref(1.0)
    
    // 浮窗相关状态
    const showFloatingPanel = ref(false)
    const floatingPanelPosition = reactive({ x: 20, y: 20 })
    const isDraggingFloatingPanel = ref(false)
    const floatingPanelDragOffset = reactive({ x: 0, y: 0 })
    
    // 以鼠标位置为中心进行缩放
    const handleWheelZoom = (event) => {
      // 检查是否按住了 Ctrl 键 (标准缩放操作习惯)
      if (event.ctrlKey || event.metaKey) {
        event.preventDefault()
        
        const canvas = canvasRef.value
        if (!canvas) return
        
        const delta = event.deltaY > 0 ? -0.1 : 0.1
        const newScale = parseFloat((canvasScale.value + delta).toFixed(1))
        
        // 限制缩放范围
        if (newScale < 0.5 || newScale > 3.0) return
        
        // 计算鼠标相对于画布可视区域的位置
        const rect = canvas.getBoundingClientRect()
        const mouseX = event.clientX - rect.left
        const mouseY = event.clientY - rect.top
        
        // 计算缩放前的实际坐标偏移 (scrollLeft/Top + mousePos) / oldScale
        // 但我们需要调整的是 scrollLeft/Top
        // 缩放中心点在内容坐标系中的位置:
        // contentX = (scrollLeft + mouseX) / oldScale
        // contentY = (scrollTop + mouseY) / oldScale
        //
        // 新的 scrollLeft 应满足:
        // (newScrollLeft + mouseX) / newScale = contentX
        // => newScrollLeft = contentX * newScale - mouseX
        //
        // 简化公式:
        // newScrollLeft = scrollLeft + mouseX * (newScale / oldScale - 1) * (oldScale / newScale)? 
        // 实际上更简单的推导：
        // 鼠标点在内容上的位置是不变的。
        // 旧的偏移: offsetOld = mouseX / oldScale
        // 新的偏移: offsetNew = mouseX / newScale
        // 实际上我们需要调整滚动条使得内容上的那个点依旧在鼠标下面。
        // 
        // 当前视口左上角在内容上的坐标: originX = scrollLeft / oldScale
        // 鼠标在内容上的坐标: pointX = (scrollLeft + mouseX) / oldScale
        // 缩放后，pointX 在屏幕上的位置应该是 mouseX
        // newScrollLeft = pointX * newScale - mouseX
        
        const scrollLeft = canvas.scrollLeft
        const scrollTop = canvas.scrollTop
        
        const pointX = (scrollLeft + mouseX) / canvasScale.value
        const pointY = (scrollTop + mouseY) / canvasScale.value
        
        canvasScale.value = newScale
        
        nextTick(() => {
            canvas.scrollLeft = pointX * newScale - mouseX
            canvas.scrollTop = pointY * newScale - mouseY
        })
      }
    }
    
    const zoomIn = () => {
      if (canvasScale.value < 3.0) {
          // 中心缩放
          const canvas = canvasRef.value
          if (canvas) {
              const centerX = canvas.clientWidth / 2
              const centerY = canvas.clientHeight / 2
              const scrollLeft = canvas.scrollLeft
              const scrollTop = canvas.scrollTop
              
              const pointX = (scrollLeft + centerX) / canvasScale.value
              const pointY = (scrollTop + centerY) / canvasScale.value
              
              canvasScale.value = parseFloat((canvasScale.value + 0.1).toFixed(1))
              
              nextTick(() => {
                  canvas.scrollLeft = pointX * canvasScale.value - centerX
                  canvas.scrollTop = pointY * canvasScale.value - centerY
              })
          } else {
              canvasScale.value = parseFloat((canvasScale.value + 0.1).toFixed(1))
          }
      }
    }
    
    const zoomOut = () => {
      if (canvasScale.value > 0.5) {
          // 中心缩放
          const canvas = canvasRef.value
          if (canvas) {
              const centerX = canvas.clientWidth / 2
              const centerY = canvas.clientHeight / 2
              const scrollLeft = canvas.scrollLeft
              const scrollTop = canvas.scrollTop
              
              const pointX = (scrollLeft + centerX) / canvasScale.value
              const pointY = (scrollTop + centerY) / canvasScale.value
              
              canvasScale.value = parseFloat((canvasScale.value - 0.1).toFixed(1))
              
              nextTick(() => {
                  canvas.scrollLeft = pointX * canvasScale.value - centerX
                  canvas.scrollTop = pointY * canvasScale.value - centerY
              })
          } else {
              canvasScale.value = parseFloat((canvasScale.value - 0.1).toFixed(1))
          }
      }
    }
    
    const resetZoom = () => {
      canvasScale.value = 1.0
    }
    
    // 左侧面板Tab状态
    const activeSidebarTab = ref('attribute') // 'attribute', 'relation', 'entity'
    
    // 切换画布大小
    const toggleCanvasSize = () => {
      isCanvasExpanded.value = !isCanvasExpanded.value
    }

    // 切换左侧面板展开/收起
    const toggleLeftPanel = () => {
      isLeftPanelExpanded.value = !isLeftPanelExpanded.value
    }

    // 浮窗拖拽相关方法
    const startDragFloatingPanel = (e) => {
      isDraggingFloatingPanel.value = true
      floatingPanelDragOffset.x = e.clientX - floatingPanelPosition.x
      floatingPanelDragOffset.y = e.clientY - floatingPanelPosition.y
      
      document.addEventListener('mousemove', dragFloatingPanel)
      document.addEventListener('mouseup', stopDragFloatingPanel)
    }
    
    const dragFloatingPanel = (e) => {
      if (isDraggingFloatingPanel.value) {
        floatingPanelPosition.x = e.clientX - floatingPanelDragOffset.x
        floatingPanelPosition.y = e.clientY - floatingPanelDragOffset.y
      }
    }
    
    const stopDragFloatingPanel = () => {
      isDraggingFloatingPanel.value = false
      document.removeEventListener('mousemove', dragFloatingPanel)
      document.removeEventListener('mouseup', stopDragFloatingPanel)
    }

    // 加载已有实体类型列表
    const loadExistingEntityTypes = async () => {
      loadingEntityTypes.value = true
      try {
        const res = await listModels()
        existingEntityTypes.value = (res.data || []).map(model => ({
          id: model.id || model.modelId,
          name: model.name || '未命名模型',
          description: model.description || '',
          modelData: model // 保存完整模型数据，用于后续加载属性
        }))
        ElMessage.success(`已加载 ${existingEntityTypes.value.length} 个已有实体类型`)
      } catch (err) {
        console.error(err)
        ElMessage.error('加载已有实体类型失败')
      } finally {
        loadingEntityTypes.value = false
      }
    }

    // 导入整模型到画布（含多实体与关系）
    const importModelToCanvas = (modelData, dropX, dropY) => {
      const entities = modelData.entities || []
      const relations = modelData.relations || []
      if (!entities.length) {
        ElMessage.warning('该模型不包含实体')
        return
      }
      const idMap = {} // 原模型entityId -> 新画布entityId
      const gridCols = 3
      const nodeW = 220
      const nodeH = 220
      let index = 0
      entities.forEach(ed => {
        const col = index % gridCols
        const row = Math.floor(index / gridCols)
        const px = Math.max(0, (dropX || 100) + col * (nodeW + 40))
        const py = Math.max(0, (dropY || 100) + row * (nodeH + 40))
        const newId = `entity-${Date.now()}-${Math.random()}`
        idMap[ed.id] = newId
        let newName = ed.name || '实体'
        const taken = new Set(canvasEntities.value.map(e => e.name))
        if (taken.has(newName)) {
          let suffix = 1
          while (taken.has(`${newName}-${suffix}`)) suffix++
          newName = `${newName}-${suffix}`
        }
        const newEntity = {
          id: newId,
          name: newName,
          x: typeof ed.x === 'number' ? ed.x : px,
          y: typeof ed.y === 'number' ? ed.y : py,
          description: ed.description || '',
          attributes: (ed.attributes || []).map(attr => ({
            id: `attr-${Date.now()}-${Math.random()}`,
            name: attr.name || attr.code,
            type: attr.type || 'text',
            required: !!attr.required,
            description: attr.description || '',
            options: (() => {
              try {
                return attr.options ? JSON.parse(attr.options) : []
              } catch { return [] }
            })()
          }))
        }
        canvasEntities.value.push(newEntity)
        index++
      })
      // 建立关系
      relations.forEach(r => {
        const sourceId = idMap[r.sourceId]
        const targetId = idMap[r.targetId]
        const sourceEntity = canvasEntities.value.find(e => e.id === sourceId)
        const targetEntity = canvasEntities.value.find(e => e.id === targetId)
        if (!sourceEntity || !targetEntity) return
        const entityWidth = 200
        const entityHeight = 200
        const dx = targetEntity.x - sourceEntity.x
        const dy = targetEntity.y - sourceEntity.y
        let sourcePoint, targetPoint
        if (Math.abs(dx) > Math.abs(dy)) {
          if (dx > 0) {
            sourcePoint = { x: sourceEntity.x + entityWidth, y: sourceEntity.y + entityHeight / 2 }
            targetPoint = { x: targetEntity.x, y: targetEntity.y + entityHeight / 2 }
          } else {
            sourcePoint = { x: sourceEntity.x, y: sourceEntity.y + entityHeight / 2 }
            targetPoint = { x: targetEntity.x + entityWidth, y: targetEntity.y + entityHeight / 2 }
          }
        } else {
          if (dy > 0) {
            sourcePoint = { x: sourceEntity.x + entityWidth / 2, y: sourceEntity.y + entityHeight }
            targetPoint = { x: targetEntity.x + entityWidth / 2, y: targetEntity.y }
          } else {
            sourcePoint = { x: sourceEntity.x + entityWidth / 2, y: sourceEntity.y }
            targetPoint = { x: targetEntity.x + entityWidth / 2, y: targetEntity.y + entityHeight }
          }
        }
        const id = `relation-${Date.now()}-${Math.random()}`
        const rel = {
          id,
          name: r.name || '关联',
          relationTypeId: null,
          sourceId: sourceEntity.id,
          targetId: targetEntity.id,
          relationType: r.type || 'one-to-many',
          description: r.description || '',
          source: { id: sourceEntity.id, x: sourcePoint.x, y: sourcePoint.y },
          target: { id: targetEntity.id, x: targetPoint.x, y: targetPoint.y }
        }
        canvasRelations.value.push(rel)
      })
      nextTick(() => {
        ElMessage.success(`已导入模型 "${modelData.name || ''}" 到画布`)
      })
    }

    // 处理已有模型的拖拽释放为整模型导入
    const handleDropEntityType = async (entityType, x, y) => {
      try {
        let modelData = entityType.modelData
        const needFetch =
          !modelData ||
          !Array.isArray(modelData.entities) ||
          modelData.entities.length === 0
        if (needFetch && entityType.id) {
          ElMessage.info('正在加载模型定义...')
          const res = await getModelDef(entityType.id)
          modelData = res.data
        }
        if (!modelData) {
          ElMessage.error('无法获取模型详情')
          return
        }
        importModelToCanvas(modelData, x, y)
      } catch (err) {
        console.error(err)
        ElMessage.error('添加模型失败：' + (err?.message || '未知错误'))
      }
    }
    
    // 选择关系类型
    const selectRelationType = (relation) => {
      selectedRelationType.value = relation
      ElMessage.success(`已选择关系类型: ${relation.name}`)
    }
    
    // 快速创建关系（双击关系组件）
    const startQuickCreateRelation = (relation) => {
      if (canvasEntities.value.length < 2) {
        ElMessage.warning('至少需要两个实体才能创建关系')
        return
      }
      selectedRelationType.value = relation
      ElMessage.info(`已选择关系类型: ${relation.name}，请点击源实体的连接点开始创建关系`)
    }

    const focusRelationTypeById = (typeId) => {
      if (!typeId) return
      const matchedType = relationComponents.value.find(item => item.id === typeId)
      if (matchedType) {
        selectedRelationType.value = matchedType
      }
    }

    const getRelationStrokeColor = (relation) => {
      if (!relation) return '#409EFF'
      const isSelected = selectedElement.value?.id === relation.id && selectedElementType.value === 'relation'
      if (isSelected) {
        return '#67C23A'
      }
      if (selectedRelationType.value?.id) {
        return relation.relationTypeId === selectedRelationType.value.id ? '#E6A23C' : '#B3C0D1'
      }
      return '#409EFF'
    }

    const getRelationStrokeWidth = (relation) => {
      if (!relation) return 2
      const isSelected = selectedElement.value?.id === relation.id && selectedElementType.value === 'relation'
      if (isSelected) {
        return 3
      }
      if (selectedRelationType.value?.id) {
        return relation.relationTypeId === selectedRelationType.value.id ? 3 : 1.5
      }
      return 2
    }

    const getRelationTextColor = (relation) => {
      return getRelationStrokeColor(relation)
    }
    
    // 高亮连接点 - 优化版，不改变颜色，只放大
    const highlightConnectionPoint = (event) => {
      if (event.target && !creatingRelation.value) {
        // 只在非创建关系状态下高亮，使用蓝色而不是绿色
        event.target.style.transform = 'scale(1.3)'
        event.target.style.boxShadow = '0 0 0 4px rgba(64, 158, 255, 0.5)'
      }
    }
    
    // 取消高亮连接点 - 确保及时释放
    const unhighlightConnectionPoint = (event) => {
      if (event.target) {
        // 立即恢复，不检查 creatingRelation 状态
        event.target.style.transform = ''
        event.target.style.background = ''
        event.target.style.boxShadow = ''
        // 移除可能残留的类
        event.target.classList.remove('connection-point-highlighted')
      }
    }

    // 清理所有连接点状态 - 统一清理函数
    const cleanupConnectionPoints = () => {
      const allConnectionPoints = document.querySelectorAll('.connection-point')
      allConnectionPoints.forEach(point => {
        point.style.transform = ''
        point.style.background = ''
        point.style.boxShadow = ''
        point.classList.remove('connection-point-highlighted')
      })
      
      // 清理源实体标记
      const sourceEntities = document.querySelectorAll('.source-entity')
      sourceEntities.forEach(el => {
        el.classList.remove('source-entity')
        el.style.boxShadow = ''
      })
    }

    // 获取用户角色
    const userRole = computed(() => store.state.user?.role || 'user')
    const isAdminOrAdvanced = computed(() =>
      userRole.value === 'admin' || userRole.value === 'advanced'
    )
    
    // 合并所有事件监听器清理逻辑到一个onBeforeUnmount钩子中
    // 移除重复的钩子，保留在代码末尾的那个

    // 添加自定义属性
    const addCustomAttribute = () => {
      if (!newAttributeName.value.trim()) {
        ElMessage.warning('请输入属性名称')
        return
      }

      attributeComponents.value.push({
        id: `attr-${Date.now()}`,
        name: newAttributeName.value.trim()
      })

      newAttributeName.value = ''
      ElMessage.success('属性添加成功')
    }

    // 添加自定义关系
    const addCustomRelation = () => {
      if (!newRelationName.value.trim()) {
        ElMessage.warning('请输入关系名称')
        return
      }

      relationComponents.value.push({
        id: `rel-${Date.now()}`,
        name: newRelationName.value.trim()
      })

      newRelationName.value = ''
      ElMessage.success('关系添加成功')
    }

    // 处理拖拽开始
    const handleDragStart = (event, type, data) => {
      event.dataTransfer.setData('application/json', JSON.stringify({
        type,
        data
      }))
      // 添加拖拽视觉反馈
      event.target.style.opacity = '0.6'
      event.target.style.transform = 'scale(0.95)'
    }

    // 处理拖拽悬浮
    const handleDragOver = (event) => {
      event.preventDefault()
    }

    // 处理拖拽释放
    const handleDrop = async (event) => {
      const data = JSON.parse(event.dataTransfer.getData('application/json'))
      const canvasRect = canvasRef.value.getBoundingClientRect()
      const x = (event.clientX - canvasRect.left) / canvasScale.value
      const y = (event.clientY - canvasRect.top) / canvasScale.value

      // 恢复拖拽元素样式
      const dragElements = document.querySelectorAll('.draggable-item')
      dragElements.forEach(el => {
        el.style.opacity = '1'
        el.style.transform = 'none'
      })

      if (data.type === 'attribute') {
        // 查找选中的实体
        const targetEntity = canvasEntities.value.find(entity => {
          return x > entity.x && x < entity.x + 200 && y > entity.y && y < entity.y + 200
        })

        if (targetEntity) {
          // 检查属性是否已存在
          const exists = targetEntity.attributes.some(attr => attr.name === data.data.name)
          if (!exists) {
            targetEntity.attributes.push({
              id: `attr-${Date.now()}`,
              name: data.data.name,
              type: 'text',
              required: false
            })
            ElMessage.success(`已为【${targetEntity.name}】添加属性 "${data.data.name}"`)
          } else {
            ElMessage.warning(`【${targetEntity.name}】中属性 "${data.data.name}" 已存在`)
          }
        } else {
          ElMessage.warning('请将属性拖拽到画布中的实体上')
        }
      } else if (data.type === 'relation') {
        ElMessage.info('请使用实体上的连接点创建关系')
      } else if (data.type === 'entityType') {
        // 处理已有实体类型的拖拽
        await handleDropEntityType(data.data, x, y)
      }
    }

    // 实体拖拽相关方法
    // 在setup函数中，替换现有的拖拽相关方法

// 实体拖拽相关方法 - 优化版本
const handleEntityMouseDown = (event, entity) => {
  // 立即停止事件传播
  event.stopPropagation()
  event.preventDefault()

  // 检查是否点击了连接点
  const isConnectionPoint = event.target.classList.contains('connection-point') ||
    event.target.closest('.connection-point')
  if (isConnectionPoint) {
    return
  }

  // 参数验证
  if (!entity || typeof entity !== 'object' || !entity.id) {
    console.warn('无效的实体对象:', entity)
    return
  }

      // 取消当前可能正在进行的关系创建
      if (creatingRelation.value) {
        creatingRelation.value = null
        document.removeEventListener('mousemove', handleRelationMouseMove)
        document.removeEventListener('mouseup', handleRelationMouseUp)
        cleanupConnectionPoints()
        // 清理data属性
        if (canvasRef.value) {
          canvasRef.value.style.cursor = 'default'
          delete canvasRef.value.dataset.creatingRelation
        }
        document.body.style.cursor = 'default'
        delete document.body.dataset.creatingRelation
      }

  // 设置拖拽状态
  isDragging.value = true
  selectedElement.value = entity
  selectedElementType.value = 'entity'

  try {
    // 计算鼠标在实体内的相对位置
    const rect = event.currentTarget.getBoundingClientRect()
    dragOffset.x = (event.clientX - rect.left) / canvasScale.value
    dragOffset.y = (event.clientY - rect.top) / canvasScale.value
    
    console.log('拖拽开始:', { 
      entityId: entity.id, 
      offset: { x: dragOffset.x, y: dragOffset.y },
      entityPosition: { x: entity.x, y: entity.y }
    })
    
    // 清理旧的事件监听器
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
    
    // 添加新的事件监听器
    document.addEventListener('mousemove', handleMouseMove)
    document.addEventListener('mouseup', handleMouseUp)

    // 视觉反馈
    if (event.currentTarget) {
      event.currentTarget.style.zIndex = '1000'
      event.currentTarget.style.cursor = 'grabbing'
      event.currentTarget.style.boxShadow = '0 8px 24px rgba(0,0,0,0.2)'
      event.currentTarget.style.transform = 'scale(1.02)'
    }
  } catch (error) {
    console.error('拖拽开始出错:', error)
    isDragging.value = false
  }
}

// 拖拽动画帧请求ID
    let dragAnimationFrameId = null

    const handleMouseMove = (event) => {
      // 检查拖拽状态
      if (!isDragging.value || !selectedElement.value) {
        return
      }

      try {
        // 阻止默认行为
        event.preventDefault()
        
        // 如果已经有待处理的帧，取消它
        if (dragAnimationFrameId) {
          cancelAnimationFrame(dragAnimationFrameId)
        }

        // 使用 requestAnimationFrame 优化性能
        dragAnimationFrameId = requestAnimationFrame(() => {
            // 设置拖拽光标
            document.body.style.cursor = 'grabbing'
            document.body.style.userSelect = 'none'

            // 获取画布
            const canvas = canvasRef.value
            if (!canvas) return

            // 获取画布位置
            const canvasRect = canvas.getBoundingClientRect()
            
            // 计算新位置 - 使用更精确的计算方法
            let newX = (event.clientX - canvasRect.left) / canvasScale.value - dragOffset.x
            let newY = (event.clientY - canvasRect.top) / canvasScale.value - dragOffset.y
            
            // 边界检查 - 确保实体不会完全移出画布
            const entityWidth = 200
            const entityHeight = 200
            const canvasWidth = 3000 // 使用固定的大画布尺寸
            const canvasHeight = 3000
            
            // 限制在画布范围内
            newX = Math.max(0, Math.min(newX, canvasWidth - entityWidth))
            newY = Math.max(0, Math.min(newY, canvasHeight - entityHeight))
            
            // 更新实体位置
            selectedElement.value.x = newX
            selectedElement.value.y = newY
            
            // 立即更新关联关系的位置
            updateRelationsPosition(selectedElement.value)
            
            dragAnimationFrameId = null
        })
        
      } catch (error) {
        console.error('拖拽移动出错:', error)
      }
    }

// 删除重复的handleMouseUp函数定义

// 删除重复的updateRelationsPosition函数定义

// 在模板中添加拖拽提示
// 在 entity-node 的 entity-footer 中修改文本：
// <el-text size="small" type="secondary">拖拽移动 | 点击配置</el-text>

    // 删除重复的handleMouseMove函数定义

    const handleMouseUp = (event) => {
      console.log('鼠标释放，结束拖拽')
      
      // 恢复默认光标
      document.body.style.cursor = 'default'
      
      // 停止拖拽状态
      isDragging.value = false

      // 事件监听器将在函数末尾统一清理
      
      // 恢复所有实体节点的样式
      try {
        const entityElements = document.querySelectorAll('.entity-node')
        entityElements.forEach(el => {
          // 重置所有实体节点的样式
          el.style.zIndex = '10'
          el.style.cursor = 'grab'
          el.style.boxShadow = '0 2px 12px rgba(0,0,0,0.1)'
        })
      } catch (error) {
        console.error('重置实体样式时出错:', error)
      }
      
      // 记录最终位置
      if (selectedElement.value && selectedElement.value.id) {
        console.log('实体拖拽结束:', { 
          id: selectedElement.value.id, 
          finalPosition: { 
            x: selectedElement.value.x, 
            y: selectedElement.value.y 
          } 
        })
      }

      // 移除事件监听
      document.removeEventListener('mousemove', handleMouseMove)
      document.removeEventListener('mouseup', handleMouseUp)
    }

    // 关系创建相关方法 - 增强版
    const startCreateRelation = (event, sourceEntity, position) => {
      console.log('======= 开始创建关系 =======')
      console.log('参数检查:', { 
        sourceEntityId: sourceEntity?.id, 
        position, 
        hasEvent: !!event,
        hasSourceEntity: !!sourceEntity,
        hasCanvasRef: !!canvasRef.value
      })
      
      // 确保事件存在
      if (!event) {
        console.error('事件对象不存在')
        return
      }

      if (!selectedRelationType.value) {
        ElMessage.warning('请先在左侧选择关系类型')
        return
      }
      
      // 立即停止事件传播，防止触发其他事件
      event.stopPropagation()
      event.preventDefault()

      // 取消当前可能正在进行的拖拽
      isDragging.value = false
      document.removeEventListener('mousemove', handleMouseMove)
      document.removeEventListener('mouseup', handleMouseUp)
      
      // 验证必要参数
      if (!sourceEntity || !sourceEntity.id) {
        console.error('无效的源实体:', sourceEntity)
        return
      }
      
      if (!canvasRef.value) {
        console.error('画布引用不存在')
        return
      }

      try {
        const canvas = canvasRef.value
        const canvasRect = canvas.getBoundingClientRect()
        
        console.log('画布信息:', { 
          canvasRect, 
          canvasId: canvas.id,
          canvasClass: canvas.className
        })
        
        // 安全获取entityRect
        const connectionPoint = event.currentTarget
        console.log('连接点信息:', { 
          connectionPoint, 
          hasParent: !!connectionPoint?.parentElement
        })
        
        if (!connectionPoint || !connectionPoint.parentElement) {
          console.error('无法获取连接点或其父元素')
          return
        }
        
        const entityRect = connectionPoint.parentElement.getBoundingClientRect()
        console.log('源实体矩形信息:', entityRect)

        // 计算连接点坐标
        let startX, startY
        const entityWidth = 200
        const entityHeight = 200

        // 根据位置计算连接点坐标
        switch (position) {
          case 'top':
            startX = (entityRect.left - canvasRect.left) + entityWidth / 2
            startY = (entityRect.top - canvasRect.top)
            break
          case 'right':
            startX = (entityRect.left - canvasRect.left) + entityWidth
            startY = (entityRect.top - canvasRect.top) + entityHeight / 2
            break
          case 'bottom':
            startX = (entityRect.left - canvasRect.left) + entityWidth / 2
            startY = (entityRect.top - canvasRect.top) + entityHeight
            break
          case 'left':
            startX = (entityRect.left - canvasRect.left)
            startY = (entityRect.top - canvasRect.top) + entityHeight / 2
            break
          default:
            // 默认使用右侧中间作为连接点
            startX = (entityRect.left - canvasRect.left) + entityWidth
            startY = (entityRect.top - canvasRect.top) + entityHeight / 2
        }

        console.log('计算的起始坐标:', { startX, startY, position })

        // 清理之前可能存在的关系创建状态
        if (creatingRelation.value) {
          console.log('清理之前的关系创建状态')
          document.removeEventListener('mousemove', handleRelationMouseMove)
          document.removeEventListener('mouseup', handleRelationMouseUp)
        }

        // 设置新的关系创建状态
        creatingRelation.value = {
          sourceId: sourceEntity.id,
          sourceEntity: sourceEntity, // 保存完整的源实体引用
          position: position,
          startX,
          startY,
          currentX: startX,
          currentY: startY,
          createdAt: Date.now()
        }

        console.log('关系创建状态已设置:', creatingRelation.value)

        // 移除旧的事件监听器，防止重复绑定
        document.removeEventListener('mousemove', handleRelationMouseMove)
        document.removeEventListener('mouseup', handleRelationMouseUp)
        
        // 添加新的事件监听
        console.log('绑定事件监听器')
        document.addEventListener('mousemove', handleRelationMouseMove)
        document.addEventListener('mouseup', handleRelationMouseUp)
        
        // 强制设置十字光标 - 使用更强的方式
        console.log('设置十字光标')
        canvas.style.cursor = 'crosshair !important'
        document.body.style.cursor = 'crosshair !important'
        
        // 强制设置data属性确保CSS规则生效
        canvas.dataset.creatingRelation = 'true'
        document.body.dataset.creatingRelation = 'true'
        
        // 强制触发CSS重新计算
        void canvas.offsetWidth
        
        // 临时高亮源实体（使用蓝色而不是绿色）
        if (connectionPoint.parentElement) {
          connectionPoint.parentElement.classList.add('source-entity')
          connectionPoint.parentElement.style.boxShadow = '0 0 0 2px #409EFF'
          setTimeout(() => {
            if (connectionPoint.parentElement) {
              connectionPoint.parentElement.style.boxShadow = ''
            }
          }, 300)
        }
        
        console.log('关系创建初始化完成')
      } catch (error) {
        console.error('开始创建关系时出错:', error)
        creatingRelation.value = null
        
        // 清理连接点状态
        cleanupConnectionPoints()
        
        // 错误恢复
        if (canvasRef.value) {
          canvasRef.value.style.cursor = 'default'
          delete canvasRef.value.dataset.creatingRelation
        }
        document.body.style.cursor = 'default'
        delete document.body.dataset.creatingRelation
      }
    }

    const handleRelationMouseMove = (event) => {
      // 确保关系创建状态存在
      if (!creatingRelation.value) {
        console.log('关系创建状态不存在，停止移动处理')
        return
      }

      try {
        const canvas = canvasRef.value
        if (!canvas) return
        
        // 加强十字光标设置，确保光标始终保持十字形状
        // 1. 设置画布光标
        canvas.style.cursor = 'crosshair !important'
        // 2. 设置body光标
        document.body.style.cursor = 'crosshair !important'
        // 3. 强制设置data属性确保CSS规则生效
        canvas.dataset.creatingRelation = 'true'
        document.body.dataset.creatingRelation = 'true'
        // 4. 强制触发CSS重新计算
        void canvas.offsetWidth // 触发重绘
        
        console.log('设置十字光标')

        // 获取画布位置信息
        const canvasRect = canvas.getBoundingClientRect()
        
        // 获取容器的滚动偏移量
        const scrollContainer = canvas.parentElement || canvas
        const scrollLeft = scrollContainer.scrollLeft || 0
        const scrollTop = scrollContainer.scrollTop || 0
        console.log('关系移动时容器滚动偏移:', { scrollLeft, scrollTop })

        // 计算相对于画布的位置 - 不添加滚动偏移，getBoundingClientRect已经考虑了滚动
        const newX = event.clientX - canvasRect.left
        const newY = event.clientY - canvasRect.top
        
        creatingRelation.value.currentX = newX
        creatingRelation.value.currentY = newY
        
        // 可选：检查是否悬停在某个实体上，提供视觉反馈
        const hoveredEntity = findEntityAtPosition(newX, newY)
        if (hoveredEntity && hoveredEntity.id !== creatingRelation.value.sourceId) {
          // 可以在这里添加额外的视觉反馈
          console.log('悬停在目标实体上:', hoveredEntity.id)
        }
      } catch (error) {
        console.error('关系创建过程中鼠标移动出错:', error)
        // 即使出错也要保持光标状态
        if (canvasRef.value) {
          canvasRef.value.style.cursor = 'crosshair'
        }
        document.body.style.cursor = 'crosshair'
      }
    }

    const handleRelationMouseUp = (event) => {
      console.log('鼠标释放，结束关系创建')
      
      // 确保关系创建状态存在
      if (!creatingRelation.value) {
        console.log('关系创建状态不存在，结束处理')
        return
      }

      try {
        const canvas = canvasRef.value
        if (!canvas) {
          console.warn('画布引用不存在')
          return
        }
        
        // 恢复默认光标
        canvas.style.cursor = 'default'
        document.body.style.cursor = 'default'

        // 计算释放位置
        const canvasRect = canvas.getBoundingClientRect()
        const targetX = event.clientX - canvasRect.left
        const targetY = event.clientY - canvasRect.top
        
        // 查找目标实体 - 使用增强版查找函数
        const targetEntity = findEntityAtPosition(targetX, targetY)
        const relationInfo = creatingRelation.value
        
        console.log('关系创建检查:', { 
          hasTarget: !!targetEntity, 
          sameEntity: targetEntity?.id === relationInfo.sourceId,
          sourceId: relationInfo.sourceId,
          targetId: targetEntity?.id,
          targetX, targetY
        })

        // 创建关系 - 确保目标实体存在且不是源实体本身
        if (targetEntity && targetEntity.id !== relationInfo.sourceId) {
          const sourceEntity = canvasEntities.value.find(e => e.id === relationInfo.sourceId)
          if (sourceEntity) {
            console.log('创建关系:', { sourceId: sourceEntity.id, targetId: targetEntity.id })
            
            // 简化连接点计算，使用更直接的方法
            const entityWidth = 200
            const entityHeight = 200
            
            // 计算源实体和目标实体的相对位置
            const dx = targetEntity.x - sourceEntity.x
            const dy = targetEntity.y - sourceEntity.y
            
            // 基于相对位置选择最佳连接点
            let sourcePoint, targetPoint
            
            // 根据水平和垂直距离判断主要方向
            if (Math.abs(dx) > Math.abs(dy)) {
              // 水平方向为主
              if (dx > 0) {
                // 目标在右侧
                sourcePoint = { x: sourceEntity.x + entityWidth, y: sourceEntity.y + entityHeight / 2 }
                targetPoint = { x: targetEntity.x, y: targetEntity.y + entityHeight / 2 }
              } else {
                // 目标在左侧
                sourcePoint = { x: sourceEntity.x, y: sourceEntity.y + entityHeight / 2 }
                targetPoint = { x: targetEntity.x + entityWidth, y: targetEntity.y + entityHeight / 2 }
              }
            } else {
              // 垂直方向为主
              if (dy > 0) {
                // 目标在下方
                sourcePoint = { x: sourceEntity.x + entityWidth / 2, y: sourceEntity.y + entityHeight }
                targetPoint = { x: targetEntity.x + entityWidth / 2, y: targetEntity.y }
              } else {
                // 目标在上方
                sourcePoint = { x: sourceEntity.x + entityWidth / 2, y: sourceEntity.y }
                targetPoint = { x: targetEntity.x + entityWidth / 2, y: targetEntity.y + entityHeight }
              }
            }
            
            console.log('使用智能连接点:', { sourcePoint, targetPoint })
            
            // 使用计算出的智能连接点创建关系
            createRelation(sourceEntity, targetEntity, sourcePoint.x, sourcePoint.y, targetPoint.x, targetPoint.y)
          } else {
            console.warn('未找到源实体:', relationInfo.sourceId)
            ElMessage.error('找不到源实体')
          }
        } else if (targetEntity && targetEntity.id === relationInfo.sourceId) {
          console.warn('不能连接实体到自身')
          ElMessage.warning('不能连接实体到自身')
        } else {
          console.warn('未找到有效的目标实体')
          // 可选：可以给用户一些提示
        }
      } catch (error) {
        console.error('创建关系时出错:', error)
        ElMessage.error('创建关系失败')
      } finally {
        // 重置状态 - 无论成功与否都必须清理
        console.log('清理关系创建状态')
        creatingRelation.value = null
        
        // 清理连接点状态
        cleanupConnectionPoints()
        
        // 移除事件监听器 - 防止内存泄漏
        document.removeEventListener('mousemove', handleRelationMouseMove)
        document.removeEventListener('mouseup', handleRelationMouseUp)
        
        // 清理所有连接点状态
        cleanupConnectionPoints()
        
        // 确保光标恢复正常
        if (canvasRef.value) {
          canvasRef.value.style.cursor = 'default'
          // 移除data属性
          if (canvasRef.value.classList.contains('canvas-area')) {
            delete canvasRef.value.dataset.creatingRelation
          }
        }
        document.body.style.cursor = 'default'
        // 移除body上的data属性
        delete document.body.dataset.creatingRelation
      }
    }

    // 根据坐标查找实体 - 增强版，增加容错和边界检查
    const findEntityAtPosition = (x, y) => {
      console.log('findEntityAtPosition:', x, y)
      
      if (!canvasEntities.value || canvasEntities.value.length === 0) {
        console.warn('没有实体可查找')
        return null
      }
      
      // 使用更宽松的检测范围，确保用户容易点击到实体
      const tolerance = 20 // 增加容错范围
      
      // 先尝试精确匹配
      let hitEntity = canvasEntities.value.find(entity => {
        if (!entity || typeof entity.x !== 'number' || typeof entity.y !== 'number') {
          console.warn('无效的实体数据:', entity)
          return false
        }
        
        const entityWidth = entity.width || 200
        const entityHeight = entity.height || 200
        return x >= entity.x && x <= entity.x + entityWidth && y >= entity.y && y <= entity.y + entityHeight
      })
      
      // 如果精确匹配失败，尝试宽松匹配
      if (!hitEntity) {
        hitEntity = canvasEntities.value.find(entity => {
          if (!entity || typeof entity.x !== 'number' || typeof entity.y !== 'number') {
            return false
          }
          
          const entityWidth = entity.width || 200
          const entityHeight = entity.height || 200
          return x >= entity.x - tolerance && 
                 x <= entity.x + entityWidth + tolerance && 
                 y >= entity.y - tolerance && 
                 y <= entity.y + entityHeight + tolerance
        })
      }
      
      if (hitEntity) {
        console.log('命中实体:', hitEntity.id, hitEntity.name)
      } else {
        console.log('未命中任何实体')
      }
      
      return hitEntity
    }

    // 创建关系 - 增强版
    const createRelation = (source, target, startX, startY, endX, endY) => {
      console.log('createRelation调用参数:', { source, target, startX, startY, endX, endY })
      
      // 参数验证
      if (!source || !target || !source.id || !target.id) {
        console.error('无效的源或目标实体')
        ElMessage.error('实体参数无效')
        return
      }
      
      if (source.id === target.id) {
        console.warn('不能连接实体到自身')
        ElMessage.warning('不能连接实体到自身')
        return
      }
      const selectedTypeId = selectedRelationType.value?.id || null
      const selectedTypeName = selectedRelationType.value?.name || 'one-to-many'
      const duplicate = canvasRelations.value.find(r =>
        r.sourceId === source.id &&
        r.targetId === target.id &&
        (r.relationTypeId === selectedTypeId || r.relationType === selectedTypeName)
      )
      if (duplicate) {
        ElMessage.warning('相同类型的关系已存在')
        return
      }
      
      const id = `relation-${Date.now()}`
      // 使用选中的关系类型名称，如果没有选中则使用默认名称
      const relationName = selectedRelationType.value?.name || '关联'
      const newRelation = {
        id,
        name: relationName,
        relationTypeId: selectedRelationType.value?.id || null,
        sourceId: source.id,
        targetId: target.id,
        relationType: selectedTypeName,
        description: '',
        source: { id: source.id, x: startX, y: startY },
        target: { id: target.id, x: endX, y: endY }
      }

      // 确保canvasRelations存在
      if (!canvasRelations.value) {
        canvasRelations.value = []
      }
      
      // 使用响应式方式添加关系
      canvasRelations.value.push(newRelation)
      console.log('关系创建成功:', newRelation)
      
      // 立即更新关系位置，使用智能连接点
      nextTick(() => {
        updateRelationsPosition(source)
        updateRelationsPosition(target)
      })
      
      ElMessage.success('关系创建成功，可在配置面板修改名称和类型')
    }

    // 更新关系位置 - 智能连接点版本（支持多重关系）
    const updateRelationsPosition = (entity) => {
      // console.log('updateRelationsPosition调用:', entity?.id)
      
      if (!canvasRelations.value || !canvasEntities.value) {
        return
      }
      
      const entityWidth = 200
      const entityHeight = 200
      
      // 1. 将关系按 (EntityA, EntityB) 对应的无序对分组
      const groups = {}
      
      canvasRelations.value.forEach(relation => {
        if (!relation) return
        const id1 = relation.sourceId
        const id2 = relation.targetId
        // 确保无序对key一致
        const key = id1 < id2 ? `${id1}-${id2}` : `${id2}-${id1}`
        
        if (!groups[key]) groups[key] = []
        groups[key].push(relation)
      })
      
      // 2. 对每一组关系计算位置
      Object.values(groups).forEach(group => {
        const count = group.length
        const spacing = 30 // 关系线之间的间距
        
        group.forEach((relation, index) => {
           const sourceEntity = canvasEntities.value.find(e => e.id === relation.sourceId)
           const targetEntity = canvasEntities.value.find(e => e.id === relation.targetId)
           
           if (!sourceEntity || !targetEntity) return
           
           // 计算偏移量
           const offset = (index - (count - 1) / 2) * spacing
           
           // 计算矩形
           const sourceRect = { x: sourceEntity.x, y: sourceEntity.y, width: entityWidth, height: entityHeight }
           const targetRect = { x: targetEntity.x, y: targetEntity.y, width: entityWidth, height: entityHeight }
           
           // 计算中心
           const sourceCenter = { x: sourceRect.x + sourceRect.width/2, y: sourceRect.y + sourceRect.height/2 }
           const targetCenter = { x: targetRect.x + targetRect.width/2, y: targetRect.y + targetRect.height/2 }
           
           // 更新坐标
           if (relation.source) {
             const p = calculateOptimalConnectionPoint(targetCenter, sourceRect, offset)
             relation.source.x = p.x
             relation.source.y = p.y
           }
           if (relation.target) {
             // 对于目标点，同样使用offset。
             // 注意：如果calculateOptimalConnectionPoint逻辑一致，
             // A->B (A在左) A用offset向下，B用offset向下。平行。
             const p = calculateOptimalConnectionPoint(sourceCenter, targetRect, offset)
             relation.target.x = p.x
             relation.target.y = p.y
           }
        })
      })
    }
    
    // 计算两个矩形之间的最佳连接点
    const calculateOptimalConnectionPoint = (targetPoint, rect, offset = 0) => {
      // 计算矩形的中心点
      const centerX = rect.x + rect.width / 2
      const centerY = rect.y + rect.height / 2
      
      // 计算从矩形中心指向目标点的向量
      const dx = targetPoint.x - centerX
      const dy = targetPoint.y - centerY
      
      // 根据向量方向决定使用哪个边的中点作为连接点
      if (Math.abs(dx) > Math.abs(dy)) {
        // 水平方向为主
        if (dx > 0) {
          // 右侧
          return { x: rect.x + rect.width, y: centerY + offset }
        } else {
          // 左侧
          return { x: rect.x, y: centerY + offset }
        }
      } else {
        // 垂直方向为主
        if (dy > 0) {
          // 底部
          return { x: centerX + offset, y: rect.y + rect.height }
        } else {
          // 顶部
          return { x: centerX + offset, y: rect.y }
        }
      }
    }

    // 画布点击事件
    const handleCanvasClick = (event) => {
      if (event.target === canvasRef.value) {
        selectedElement.value = null
        selectedElementType.value = null
        selectedEntity.value = null
      }
    }

    // 选择元素
    const selectEntity = (entity) => {
      if (!entity || !entity.id) {
        console.warn('选择无效的实体:', entity)
        return
      }
      
      // 停止任何正在进行的拖拽操作
      if (isDragging.value) {
        isDragging.value = false
        document.removeEventListener('mousemove', handleMouseMove)
        document.removeEventListener('mouseup', handleMouseUp)
        document.body.style.cursor = 'default'
      }
      
      selectedElement.value = entity
      selectedElementType.value = 'entity'
      selectedEntity.value = null

      // 复制配置
      Object.assign(selectedElementConfig, {
        name: entity.name || '未命名实体',
        description: entity.description || ''
      })
    }

    const selectAttribute = (attr, entity) => {
      selectedElement.value = attr
      selectedElementType.value = 'attribute'
      selectedEntity.value = entity

      // 复制配置
      Object.assign(selectedElementConfig, {
        name: attr.name || '',
        type: attr.type || 'text',
        required: attr.required || false,
        description: attr.description || '',
        options: JSON.parse(JSON.stringify(attr.options || []))
      })
    }

    const selectRelation = (relation) => {
      selectedElement.value = relation
      selectedElementType.value = 'relation'
      selectedEntity.value = null

      // 复制配置
      Object.assign(selectedElementConfig, {
        name: relation.name || '',
        relationType: relation.relationType || 'one-to-many',
        description: relation.description || ''
      })

      if (relation.relationTypeId) {
        focusRelationTypeById(relation.relationTypeId)
      }
    }

    // 保存配置
    const saveEntityConfig = () => {
      if (!selectedElement.value || selectedElementType.value !== 'entity') return

      if (!selectedElementConfig.name.trim()) {
        ElMessage.warning('实体名称不能为空')
        return
      }

      // 更新配置
      selectedElement.value.name = selectedElementConfig.name
      selectedElement.value.description = selectedElementConfig.description

      ElMessage.success('实体配置已保存')
    }

    const saveAttributeConfig = () => {
      if (!selectedElement.value || selectedElementType.value !== 'attribute' || !selectedEntity.value) return

      if (!selectedElementConfig.name.trim()) {
        ElMessage.warning('属性名称不能为空')
        return
      }

      // 检查重名
      const nameExists = selectedEntity.value.attributes.some(attr =>
        attr.id !== selectedElement.value.id && attr.name === selectedElementConfig.name
      )

      if (nameExists) {
        ElMessage.warning('该属性名称已存在')
        return
      }

      // 更新配置
      selectedElement.value.name = selectedElementConfig.name
      selectedElement.value.type = selectedElementConfig.type
      selectedElement.value.required = selectedElementConfig.required
      selectedElement.value.description = selectedElementConfig.description

      if (['select', 'radio', 'checkbox'].includes(selectedElementConfig.type)) {
        selectedElement.value.options = JSON.parse(JSON.stringify(selectedElementConfig.options || []))
      } else {
        delete selectedElement.value.options
      }

      ElMessage.success('属性配置已保存')
    }

    const saveRelationConfig = () => {
      if (!selectedElement.value || selectedElementType.value !== 'relation') return

      if (!selectedElementConfig.name.trim()) {
        ElMessage.warning('关系名称不能为空')
        return
      }

      // 更新配置
      selectedElement.value.name = selectedElementConfig.name
      selectedElement.value.relationType = selectedElementConfig.relationType
      selectedElement.value.description = selectedElementConfig.description

      ElMessage.success('关系配置已保存')
    }

    // 保存模型 - 旧实现已移除，重定向到submitForm
    // const saveModel = () => { ... } 


    // 删除元素
    const deleteSelectedElement = () => {
      ElMessageBox.confirm('确定要删除选中的元素吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (selectedElementType.value === 'entity') {
          // 删除实体及关联关系
          const entityId = selectedElement.value.id
          canvasRelations.value = canvasRelations.value.filter(r =>
            r.sourceId !== entityId && r.targetId !== entityId
          )
          canvasEntities.value = canvasEntities.value.filter(e => e.id !== entityId)
        } else if (selectedElementType.value === 'attribute' && selectedEntity.value) {
          // 删除属性
          const attrIndex = selectedEntity.value.attributes.findIndex(
            a => a.id === selectedElement.value.id
          )
          if (attrIndex > -1) {
            selectedEntity.value.attributes.splice(attrIndex, 1)
          }
        } else if (selectedElementType.value === 'relation') {
          // 删除关系
          canvasRelations.value = canvasRelations.value.filter(r =>
            r.id !== selectedElement.value.id
          )
        }

        // 清空选择
        selectedElement.value = null
        selectedElementType.value = null
        selectedEntity.value = null

        ElMessage.success('元素已删除')
      })
    }

    // 删除属性
    const deleteAttribute = (entity, index) => {
      ElMessageBox.confirm('确定要删除这个属性吗？', '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        entity.attributes.splice(index, 1)
        ElMessage.success('属性已删除')
      })
    }

    // 选项管理
    const addOption = (config) => {
      if (!config.options) config.options = []
      config.options.push('新选项')
    }

    const deleteOption = (config, index) => {
      if (config.options && config.options.length > 1) {
        config.options.splice(index, 1)
      } else {
        ElMessage.warning('至少保留一个选项')
      }
    }

    // 辅助方法
    const getTypeName = (type) => {
      const typeMap = {
        'text': '文本',
        'number': '数字',
        'date': '日期',
        'boolean': '布尔值',
        'textarea': '长文本',
        'select': '下拉选择',
        'radio': '单选按钮',
        'checkbox': '复选框',
        'file': '文件上传'
      }
      return typeMap[type] || type
    }

    const getEntityNameById = (id) => {
      const entity = canvasEntities.value.find(e => e.id === id)
      return entity ? entity.name : '未知实体'
    }

    // 添加新实体
    const baseAttrTypeMap = {
      name: 'text',
      age: 'date'
    }
    const buildBaseAttributes = () => {
      return Object.entries(basicComponent).map(([key, label]) => ({
        id: `attr-${Date.now()}-${Math.random()}`,
        name: label,
        type: baseAttrTypeMap[key] || 'text',
        required: true,
        description: ''
      }))
    }
    const addNewEntity = () => {
      // 确保使用合理的默认位置，即使canvasRef未初始化
      let defaultX = 200
      let defaultY = 200
      
      if (canvasRef.value && canvasRef.value.clientWidth && canvasRef.value.clientHeight) {
        defaultX = Math.random() * (canvasRef.value.clientWidth - 200)
        defaultY = Math.random() * (canvasRef.value.clientHeight - 200)
      } else {
        // 使用基于现有实体数量的偏移位置，避免实体重叠
        const entityCount = canvasEntities.value.length
        defaultX = 100 + (entityCount % 5) * 220
        defaultY = 100 + Math.floor(entityCount / 5) * 220
      }
      
      const newEntity = {
        id: `entity-${Date.now()}`,
        name: '新实体',
        x: defaultX,
        y: defaultY,
        description: '',
        attributes: buildBaseAttributes()
      }

      canvasEntities.value.push(newEntity)

      nextTick(() => {
        selectEntity(newEntity)
      })
    }

    const clearCanvas = () => {
      ElMessageBox.confirm('确定要清空画布吗？所有实体和关系都将被删除', '清空确认', {
        type: 'warning'
      }).then(() => {
        canvasEntities.value = []
        canvasRelations.value = []
        selectedElement.value = null
        selectedElementType.value = null
        ElMessage.success('画布已清空')
      })
    }

    const resetForm = () => {
      selectedElement.value = null
      selectedElementType.value = null
      selectedEntity.value = null
      creatingRelation.value = null
      selectedRelationType.value = null
      isDragging.value = false
      dragOffset.x = 0
      dragOffset.y = 0
      Object.keys(selectedElementConfig).forEach((key) => {
        delete selectedElementConfig[key]
      })
      // 清理连接点状态
      cleanupConnectionPoints()
      // 清理data属性
      if (canvasRef.value) {
        canvasRef.value.style.cursor = 'default'
        delete canvasRef.value.dataset.creatingRelation
      }
      document.body.style.cursor = 'default'
      delete document.body.dataset.creatingRelation
    }

    const submitForm = async () => {
      if (canvasEntities.value.length === 0) {
        ElMessage.warning('请至少创建一个实体')
        return
      }

      loading.value = true

      try {
        const submitData = {
          name: modelName.value || '未命名模型',
          description: '用户创建的模型', // 可以添加描述输入框
          entities: canvasEntities.value.map(e => ({
            id: e.id,
            name: e.name,
            code: e.name, // 简化处理，使用name作为code
            description: e.description,
            x: Math.round(e.x),
            y: Math.round(e.y),
            attributes: e.attributes.map(a => ({
              name: a.name,
              code: a.name,
              type: a.type,
              required: a.required,
              description: a.description,
              options: JSON.stringify(a.options || [])
            }))
          })),
          relations: canvasRelations.value.map(r => ({
            name: r.name,
            type: r.relationType,
            sourceId: r.sourceId,
            targetId: r.targetId,
            description: r.description
          }))
        }

        console.log('提交数据:', submitData)
        
        await saveModelDef(submitData)

        if (isAdminOrAdvanced.value) {
          ElMessage.success('模型创建成功')
        } else {
          ElMessage.success('模型已提交审核')
        }
        resetForm()
      } catch (err) {
        console.error(err)
        ElMessage.error('操作失败：' + (err?.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }

    // 保存模型
    const saveModel = async () => {
      // 复用提交逻辑，或者区分草稿和发布状态
      await submitForm()
    }
    
    // 开始用户引导
    const startGuide = () => {
      guideStep.value = 0
      showGuideDialog.value = true
    }

    // 下一步
    const nextGuideStep = () => {
      if (guideStep.value < 2) {
        guideStep.value++
      }
    }

    // 上一步
    const prevGuideStep = () => {
      if (guideStep.value > 0) {
        guideStep.value--
      }
    }

    // 完成用户引导
    const finishGuide = () => {
      showGuideDialog.value = false
      // 保存用户已查看引导的状态到本地存储
      localStorage.setItem('modeling-guide-completed', 'true')
    }

    // 组件挂载时加载已有实体类型
    onMounted(() => {
      loadExistingEntityTypes()
      // 检查是否首次访问，如果是则自动显示引导
      const guideCompleted = localStorage.getItem('modeling-guide-completed')
      if (!guideCompleted) {
        // 延迟显示，让页面先渲染完成
        setTimeout(() => {
          startGuide()
        }, 1000)
      }
    })

    return {
      // 基础数据
      basicComponent,
      attributeComponents,
      relationComponents,
      newAttributeName,
      newRelationName,
      existingEntityTypes,
      loadingEntityTypes,

      // 画布数据
      canvasEntities,
      canvasRelations,
      selectedElement,
      selectedElementType,
      selectedElementConfig,
      canvasRef,

      // 状态和计算属性
      loading,
      isAdminOrAdvanced,
      modelName,
      isLeftPanelExpanded,

      // 方法
      addCustomAttribute,
      addCustomRelation,
      handleDragStart,
      handleDragOver,
      handleDrop,
      handleEntityMouseDown,
      handleMouseMove,
      handleMouseUp,
      selectEntity,
      selectAttribute,
      selectRelation,
      addNewEntity,
      saveAttributeConfig,
      saveEntityConfig,
      saveRelationConfig,
      submitForm,
      resetForm,
      deleteSelectedElement,
      deleteAttribute,
      addOption,
      deleteOption,
      getTypeName,
      getEntityNameById,
      clearCanvas,
      saveModel,
      startCreateRelation,
      highlightConnectionPoint,
      unhighlightConnectionPoint,
      creatingRelation,
      selectRelationType,
      startQuickCreateRelation,
      selectedRelationType,
      relationUsageMap,
      getRelationStrokeColor,
      getRelationStrokeWidth,
      getRelationTextColor,
      isCanvasExpanded,
      toggleCanvasSize,
      toggleLeftPanel,
      startDragFloatingPanel,
      dragFloatingPanel,
      stopDragFloatingPanel,
      showFloatingPanel,
      floatingPanelPosition,
      loadExistingEntityTypes,
      showGuideDialog,
      guideStep,
      startGuide,
      nextGuideStep,
      prevGuideStep,
      finishGuide,

      // 新增状态
      canvasScale,
      zoomIn,
      zoomOut,
      resetZoom,
      activeSidebarTab
    }
  }
}
</script>

<style scoped>
.modeling-type-create-view {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  position: relative;
}

/* 帮助按钮 */
.help-button {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 999;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

/* 用户引导对话框样式 */
.guide-content {
  margin: 30px 0;
  min-height: 200px;
}

.guide-step-content {
  padding: 20px;
}

.guide-step-content h3 {
  margin-bottom: 15px;
  color: #303133;
  font-size: 18px;
}

.guide-step-content ul {
  list-style: none;
  padding: 0;
}

.guide-step-content li {
  margin-bottom: 12px;
  padding-left: 24px;
  position: relative;
  line-height: 1.6;
  color: #606266;
}

.guide-step-content li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #409EFF;
  font-size: 20px;
  line-height: 1;
}

.guide-step-content strong {
  color: #303133;
}

.guide-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 主容器布局 */
.main-container {
  display: flex;
  gap: 20px;
  height: calc(100vh - 100px);
  align-items: stretch;
  position: relative;
}

/* 左侧面板 */
.left-panel-wrapper {
    display: flex;
    height: 100%;
    width: 320px; /* Wider to accommodate tabs + content */
    background: white;
    border-right: 1px solid #e4e7ed;
    position: relative;
    transition: width 0.3s;
    flex-shrink: 0;
}

.left-panel-wrapper.collapsed {
    width: 0;
    overflow: visible; /* Allow button to show */
    border-right: none;
}
.left-panel-wrapper.fullscreen-overlay {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 3001;
    box-shadow: 2px 0 12px rgba(0,0,0,0.12);
}
.left-panel-wrapper.fullscreen-overlay .side-tab-bar {
    height: 100vh;
}
.left-panel-wrapper.fullscreen-overlay .side-content-area {
    height: 100vh;
}
.left-panel-wrapper.fullscreen-overlay .panel-toggle-wrapper {
    top: 50%;
    right: -16px;
}

/* 修复全屏状态下左侧面板收起时的按钮位置 */
.left-panel-wrapper.fullscreen-overlay.collapsed .panel-toggle-wrapper {
    left: 0;
    right: auto;
    transform: translateY(-50%);
}

.side-tab-bar {
    width: 50px;
    height: 100%;
    background: #f5f7fa;
    border-right: 1px solid #e4e7ed;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 10px;
    flex-shrink: 0;
}

.tab-item {
    width: 100%;
    height: 60px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    color: #606266;
    font-size: 12px;
    gap: 4px;
    transition: all 0.2s;
    position: relative;
}

.tab-item:hover {
    color: #409EFF;
    background: #ecf5ff;
}

.tab-item.active {
    color: #409EFF;
    background: white;
    font-weight: 500;
}

.tab-item.active::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 3px;
    background: #409EFF;
}

.tab-item .el-icon {
    font-size: 20px;
}

.side-content-area {
    flex: 1;
    height: 100%;
    overflow: hidden;
    background: white;
    display: flex;
    flex-direction: column;
    min-width: 0;
}

.tab-panel-content {
    height: 100%;
    display: flex;
    flex-direction: column;
}

.tab-panel-content .panel-header-inline {
    padding: 15px;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: #fff;
}

.tab-panel-content h3 {
    margin: 0;
    font-size: 16px;
    color: #303133;
    font-weight: 600;
}

.tab-panel-content .el-scrollbar {
    flex: 1;
}

.tab-panel-content .component-section {
    padding: 15px;
}

/* 确保全屏下 z-index 足够高 */
.center-panel.panel-expanded {
    z-index: 2000 !important;
}

.center-panel.panel-expanded :deep(.el-card__body) {
    padding: 0; /* 全屏模式下移除 padding，让画布完全贴边 */
}

.canvas-scaler {
  width: 5000px;
  height: 5000px;
  transform-origin: 0 0;
  background-image: 
    linear-gradient(to right, #f0f2f5 1px, transparent 1px),
    linear-gradient(to bottom, #f0f2f5 1px, transparent 1px);
  background-size: 20px 20px;
}

/* 展开/收起按钮 */
.panel-toggle-wrapper {
  position: absolute;
  top: 50%;
  right: -16px;
  transform: translateY(-50%);
  z-index: 1002;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.panel-toggle-btn {
  width: 32px;
  height: 32px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  border: 1px solid #409EFF;
  background: white;
  transition: all 0.3s ease;
  font-size: 16px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: visible;
}

.panel-toggle-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  background: #409EFF;
  color: white;
  border-color: #409EFF;
}

.panel-toggle-btn:active {
  transform: scale(1.0);
}

.panel-toggle-btn.is-primary {
  background: #409EFF;
  color: white;
}

/* 专门为收起状态的左箭头设置黑色 */
.arrow-left-black {
  color: #303133 !important;
}

.toggle-label {
  font-size: 11px;
  color: #409EFF;
  font-weight: 500;
  white-space: nowrap;
  background: white;
  padding: 3px 6px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  border: 1px solid #409EFF;
  pointer-events: none;
  line-height: 1.2;
}

.left-panel-scroll {
  height: 100%;
}

.left-panel-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding-right: 8px;
}

:deep(.left-panel-scroll .el-scrollbar__wrap) {
  max-height: 100%;
}

:deep(.left-panel-scroll .el-scrollbar__bar.is-vertical) {
  width: 6px;
}

:deep(.left-panel-scroll .el-scrollbar__thumb) {
  background: rgba(64, 158, 255, 0.4);
  border-radius: 4px;
}

/* 中央画布面板 */
.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  transition: all 0.3s ease;
  margin-left: 0;
  width: 0; /* 初始宽度为0，让flex自动计算 */
  position: relative; /* 确保子元素绝对定位相对于此 */
}

.center-panel.panel-left-collapsed {
  margin-left: 0;
  flex: 1 1 auto;
}

.center-panel.panel-expanded {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2000 !important; /* 确保在最上层 */
  padding: 0; /* 全屏模式下去除内边距 */
  background: #f5f7fa;
  margin: 0;
  width: 100vw;
  height: 100vh;
}

/* 确保全屏下卡片填满 */
.center-panel.panel-expanded .canvas-card {
  height: 100%;
  width: 100%;
  border-radius: 0;
}

/* 确保全屏下画布区域填满 */
.center-panel.panel-expanded .canvas-area {
  height: calc(100vh - 60px); /* 减去头部高度 */
  min-height: auto;
}

/* 右侧配置面板 */
.right-panel {
  width: 360px;
  height: 100%;
  padding: 0;
  flex-direction: column;
  overflow-y: auto;
  scrollbar-width: thin;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-panel.panel-collapsed {
  width: 0;
  padding: 0;
  overflow: hidden;
  opacity: 0;
}

/* 面板通用样式 */
.panel-card {
  height: fit-content;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.panel-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.panel-icon {
  margin-right: 8px;
  color: #409EFF;
}

.mt-20 {
  margin-top: 20px;
}

.header-add-btn {
  font-weight: 600;
  margin-right: 12px;
}

.divider {
  width: 1px;
  height: 16px;
  background-color: #dcdfe6;
  margin-right: 12px;
}

.clear-btn {
  color: #f56c6c;
  font-size: 12px;
}

.canvas-header-actions {
  display: flex;
  align-items: center;
  /* gap: 8px; */
}

.resize-btn {
  color: #409EFF;
  font-size: 12px;
}

/* 组件区域样式 */
.component-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 12px;
  font-weight: 500;
  display: flex;
  align-items: center;
}

.section-title::before {
  content: '';
  display: inline-block;
  width: 4px;
  height: 14px;
  background: #409EFF;
  margin-right: 6px;
  border-radius: 2px;
}

/* 组件项样式 */
.component-item {
  padding: 8px 12px;
  margin-bottom: 8px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background-color: #f8f9fa;
  cursor: move;
  transition: all 0.2s ease;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.basic-item {
  background-color: #f0f9ff;
  border-color: #c6e2ff;
}

.draggable-item {
  cursor: grab;
}

.draggable-item:hover {
  background-color: #ecf5ff;
  border-color: #409EFF;
  transform: translateX(2px);
}

.draggable-item:active {
  cursor: grabbing;
}

.drag-icon {
  color: #909399;
  font-size: 14px;
  transition: all 0.2s;
}

.draggable-item:hover .drag-icon {
  color: #409EFF;
}

/* 关系组件项样式 */
.relation-item {
  cursor: pointer;
  position: relative;
}

.relation-item.relation-linked {
  border-color: #f5dab1;
  background-color: #fdf6ec;
}

.relation-item:hover {
  background-color: #ecf5ff;
  border-color: #409EFF;
}

.relation-item.selected-relation {
  background-color: #e1f3d8;
  border-color: #67C23A;
  border-width: 2px;
}

.relation-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.relation-usage {
  min-width: 48px;
  text-align: center;
}

.relation-icon {
  color: #909399;
  font-size: 14px;
  transition: all 0.2s;
}

.relation-item:hover .relation-icon {
  color: #409EFF;
}

/* 已有实体类型项样式 */
.entity-type-item {
  cursor: grab;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.entity-type-item:hover {
  background-color: #ecf5ff;
  border-color: #409EFF;
  transform: translateX(2px);
}

.entity-type-item:active {
  cursor: grabbing;
}

.entity-type-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.entity-type-name {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.entity-type-desc {
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 200px;
}

.empty-tip {
  padding: 20px 0;
  text-align: center;
}

.add-input {
  margin-top: 12px;
  width: 100%;
}

/* 画布区域样式 */
.canvas-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

/* 修复 Element Plus 卡片 body 样式，确保 flex 布局生效 */
.canvas-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden; /* 防止 body 撑开 */
  padding: 20px; /* 保持默认 padding，或者根据需要调整 */
  height: 100%; /* 确保高度占满 */
  box-sizing: border-box;
}

.canvas-card.card-expanded {
  height: 100%;
  border-radius: 0;
}

.canvas-area {
  flex: 1;
  background-color: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  position: relative;
  overflow: scroll; /* 强制始终显示滚动条轨道 */
  min-height: 500px;
  cursor: default;
  transition: all 0.3s;
  z-index: 0;
  padding-bottom: 0;
}

/* 滚动条样式优化 - 更明显 */
.canvas-area::-webkit-scrollbar {
  width: 14px; /* 加宽 */
  height: 14px; /* 加高 */
}

.canvas-area::-webkit-scrollbar-track {
  background: #f0f2f5; /* 轨道颜色加深 */
  border-radius: 0;
  border: 1px solid #e4e7ed; /* 增加边框 */
}

.canvas-area::-webkit-scrollbar-thumb {
  background: #909399; /* 滑块颜色加深 */
  border-radius: 7px;
  border: 2px solid #f0f2f5; /* 增加边框让滑块看起来更圆润 */
}

.canvas-area::-webkit-scrollbar-thumb:hover {
  background: #606266; /* 悬停时更深 */
}

.canvas-area::-webkit-scrollbar-corner {
  background: #f0f2f5;
}

.canvas-area.canvas-expanded {
  border-radius: 0;
  min-height: calc(100vh - 120px);
}

/* 实体节点样式 */
.entity-node {
  position: absolute;
  width: 200px;
  height: 200px;
  background-color: white;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  cursor: grab;
  transition: all 0.3s ease;
  padding: 12px;
  z-index: 20;
  user-select: none;
}

.entity-node:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  border-color: #c0c4cc;
  transform: translateY(-2px);
}

.entity-node.selected {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  background-color: #f4f9ff;
}

/* 连接点样式 - 优化版，默认显示 */
.connection-point {
  position: absolute;
  width: 12px;
  height: 12px;
  background: #409EFF; /* 默认显示蓝色 */
  border-radius: 50%;
  z-index: 100;
  cursor: crosshair;
  opacity: 1; /* 始终显示 */
  border: 2px solid white;
  box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.5);
  pointer-events: auto;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 鼠标悬停在连接点上时增强效果 */
.connection-point:hover {
  transform: scale(1.3);
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.4);
  background: #409EFF;
}

/* 创建关系过程中显示所有连接点 - 使用蓝色而不是绿色 */
body[data-creating-relation="true"] .entity-node .connection-point {
  opacity: 1 !important;
  background: #409EFF;
  box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.4);
  transform: scale(1.1);
}

body[data-creating-relation="true"] .entity-node:not(.source-entity) .connection-point {
  animation: pulse-connection 1.5s ease-in-out infinite;
}

/* 鼠标悬停在连接点上时增强效果 - 使用蓝色 */
.connection-point:hover {
  background: #409EFF !important;
  opacity: 1 !important;
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.5) !important;
  transform: scale(1.3) !important;
  z-index: 101;
}

/* 确保连接点状态能及时恢复 */
.connection-point:not(:hover) {
  background: #409EFF;
}

/* 确保在关系创建过程中光标始终为十字 */
.canvas-area {
  cursor: default;
}

.canvas-area[data-creating-relation="true"] {
  cursor: crosshair;
}

/* 确保整个文档在关系创建时也显示十字光标 */
body[data-creating-relation="true"] {
  cursor: crosshair;
}

body[data-creating-relation="true"] .entity-node {
  cursor: crosshair;
}

/* 连接点脉冲动画 - 使用蓝色 */
@keyframes pulse-connection {
  0% {
    box-shadow: 0 0 0 0 rgba(64, 158, 255, 0.5);
    transform: scale(1.1);
  }
  50% {
    box-shadow: 0 0 0 8px rgba(64, 158, 255, 0);
    transform: scale(1.15);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(64, 158, 255, 0);
    transform: scale(1.1);
  }
}

.connection-point.top {
  top: -9px;
  left: 50%;
  transform: translateX(-50%);
}

.connection-point.right {
  right: -9px;
  top: 50%;
  transform: translateY(-50%);
}

.connection-point.bottom {
  bottom: -9px;
  left: 50%;
  transform: translateX(-50%);
}

.connection-point.left {
  left: -9px;
  top: 50%;
  transform: translateY(-50%);
}

.entity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 8px;
}

.entity-name {
  font-weight: 600;
  color: #303133;
  font-size: 15px;
}

.entity-attributes {
  height: 90px;
  overflow-y: auto;
  scrollbar-width: thin;
}

.attribute-item {
  font-size: 13px;
  color: #606266;
  padding: 4px 0;
  border-bottom: 1px dotted #ebeef5;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: all 0.2s;
}

.attribute-item:hover {
  color: #409EFF;
  background-color: #f5fafe;
  padding-left: 4px;
  border-radius: 8px;
}

.attr-icon {
  font-size: 10px;
  margin-right: 4px;
  color: #909399;
}

.entity-footer {
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
  margin-top: 8px;
}

/* 添加实体按钮 */
.add-entity-btn {
  position: absolute;
  bottom: 20px;
  right: 20px;
  z-index: 200;
  pointer-events: auto;
}

/* 配置区域样式 */
.config-card {
  margin-bottom: 0;
}

.config-content {
  max-height: calc(100vh - 300px);
  overflow-y: auto;
  scrollbar-width: thin;
}

.config-section {
  padding: 10px 0;
}

.empty-config {
  padding: 40px 0;
}

.config-title {
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #eaeaea;
  font-size: 15px;
  color: #303133;
  font-weight: 600;
}

.attribute-list {
  max-height: 200px;
  overflow-y: auto;
  scrollbar-width: thin;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 8px;
}

.attribute-item-config {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.2s;
}

.attribute-item-config:hover {
  background: #ecf5ff;
}

.attribute-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.attribute-name {
  font-weight: 500;
  font-size: 13px;
}

.options-section {
  margin-top: 8px;
}

.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.config-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  gap: 8px;
  align-items: center;
}

.save-btn {
  flex: 1;
}

.config-actions .el-button {
  height: 32px;
}

/* 操作按钮区域 */
.action-buttons {
  margin-top: 0;
  margin-bottom: 0;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  justify-content: center;
  padding: 15px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  position: relative;
  z-index: 100;
  width: 100%;
  box-sizing: border-box;
}

.action-buttons.buttons-collapsed {
  display: none;
}

.model-name-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.model-name-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

.model-name-input {
  width: 200px;
  flex-shrink: 0;
}

.action-buttons .el-button {
  flex-shrink: 0;
  white-space: nowrap;
  width: 140px;
  height: 40px;
  padding: 0 20px;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

/* 使用深度选择器调整按钮内部布局，确保图标和文字作为一个整体居中 */
:deep(.action-buttons .el-button > span) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  flex-shrink: 0;
  text-align: center;
  margin: 0 auto;
}

:deep(.action-buttons .el-button .el-icon) {
  margin-right: 6px;
  flex-shrink: 0;
  width: 14px;
  height: 14px;
}

.action-buttons .el-input {
  height: 40px;
}

.action-buttons .el-input__wrapper {
  height: 40px;
  padding: 0 12px;
}

.action-buttons .el-input__inner {
  height: 40px;
  line-height: 40px;
}

/* 关系连线样式 */
.relation-lines {
  pointer-events: none;
  z-index: 1;
}

.relation-lines line {
  pointer-events: all;
  transition: all 0.3s ease;
}

.relation-lines g.selected line {
  stroke: #67C23A;
  stroke-width: 3;
}

.relation-lines text {
  pointer-events: all;
  transition: all 0.3s ease;
  user-select: none;
}

/* 响应式调整 */
@media (max-width: 1400px) {
  .right-panel {
    width: 320px;
  }
}

@media (max-width: 1200px) {
  .left-panel {
    width: 240px;
  }

  .right-panel {
    width: 280px;
  }
}

@media (max-width: 992px) {
  .main-container {
    flex-direction: column;
    height: auto;
  }

  .left-panel,
  .right-panel {
    width: 100%;
    height: auto;
  }

  .center-panel {
    height: 500px;
    margin: 20px 0;
  }

  .action-buttons {
    flex-wrap: wrap;
  }
}

/* 浮窗样式 */
.floating-config-panel {
  position: fixed;
  width: 350px;
  max-height: 80vh;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 3000;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.floating-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  cursor: move;
  user-select: none;
}

.floating-panel-header .panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 14px;
}

.floating-panel-header .close-btn-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  z-index: 3100;
}

.floating-panel-header .close-btn-wrapper:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: rotate(90deg);
}

.floating-panel-header .close-btn-inner {
  display: flex;
  align-items: center;
  justify-content: center;
}

.floating-panel-header .close-icon {
  font-size: 28px;
  font-weight: bold;
  color: white;
  line-height: 1;
}

.floating-panel-content {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.floating-panel-content .config-section {
  margin-bottom: 16px;
}

.floating-panel-content .config-title {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding-bottom: 8px;
  border-bottom: 2px solid #667eea;
}

.floating-panel-content .config-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.floating-panel-content .save-btn {
  width: calc(100% - 40px);
}

.floating-panel-content .attribute-list {
  max-height: 200px;
  overflow-y: auto;
}

.floating-panel-content .attribute-item-config {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px;
  background: #f5f7fa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.floating-panel-content .attribute-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.floating-panel-content .attribute-name {
  font-size: 13px;
  color: #606266;
}

.floating-panel-content .option-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.floating-panel-content .option-input {
  flex: 1;
  margin-right: 8px;
}

/* 浮窗开关按钮样式 */
.floating-panel-toggle {
  position: fixed;
  bottom: 30px;
  right: 100px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 30px;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  z-index: 3000;
  user-select: none;
}

.floating-panel-toggle:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.floating-panel-toggle:active {
  transform: translateY(0);
}

.floating-panel-toggle .el-icon {
  font-size: 18px;
}
</style>
