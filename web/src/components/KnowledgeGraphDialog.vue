<template>
  <el-dialog
    title="文物知识图谱"
    v-model="visible"
    width="90%"
    top="5vh"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <div class="kg-container" ref="kgRef" style="width: 100%; height: 70vh;"></div>
  </el-dialog>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const props = defineProps({
  artifactId: [String, Number]
})
const emit = defineEmits(['close'])

const visible = ref(true)
const kgRef = ref(null)
let chart = null

const loadGraphData = async () => {
  if (!props.artifactId) return
  try {
    const res = await axios.get(`/api/kg/${props.artifactId}`)
    initChart(res.data)
  } catch (err) {
    console.error('加载知识图谱失败', err)
  }
}

const initChart = (data) => {
  nextTick(() => {
    if (!chart) {
      chart = echarts.init(kgRef.value)
    }
    const option = {
      tooltip: {},
      legend: { data: ['文物', '人物', '地点', '时代', '工艺'] },
      series: [{
        type: 'graph',
        layout: 'force',
        symbolSize: 60,
        roam: true,
        edgeSymbol: ['circle', 'arrow'],
        edgeLabel: { normal: { show: true, formatter: '{c}' } },
        label: { show: true, fontSize: 14 },
        force: { repulsion: 800, edgeLength: [50, 200] },
        data: data.nodes,
        links: data.links,
        lineStyle: { color: '#409EFF', width: 2 }
      }]
    }
    chart.setOption(option)
  })
}

watch(() => props.artifactId, loadGraphData)
watch(visible, (val) => !val && emit('close'))

onMounted(() => {
  visible.value = true
  loadGraphData()
})
</script>

<style scoped>
.kg-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>