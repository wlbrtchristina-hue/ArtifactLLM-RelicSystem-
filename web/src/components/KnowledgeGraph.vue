<template>
  <div ref="graphContainer" class="knowledge-graph"></div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { getRelicGraph } from '@/api/knowledge'

export default {
  name: 'KnowledgeGraph',
  props: {
    artifactData: {
      type: Object,
      default: null
    }
  },
  setup(props) {
    const graphContainer = ref(null)
    let chart = null
    const graphData = ref(null)

    // 初始化图表
    const initChart = () => {
      if (graphContainer.value) {
        chart = echarts.init(graphContainer.value)
        updateChart()
      }
    }

    const fetchGraph = async () => {
      const name = props.artifactData?.name
      if (!name) {
        graphData.value = null
        updateChart()
        return
      }
      try {
        const res = await getRelicGraph(name)
        graphData.value = res.data || null
        updateChart()
      } catch (e) {
        graphData.value = null
        updateChart()
      }
    }

    // 更新图表数据
    const updateChart = () => {
      if (!chart) return

      // 优先使用后端返回的知识图谱数据
      if (graphData.value && Array.isArray(graphData.value.nodes)) {
        const nodes = graphData.value.nodes.map(n => {
          const size = n.symbolSize || (n.category === 0 ? 80 : 60)
          const fontSize = Math.max(12, Math.round(size * 0.28))
          return {
            id: n.id,
            name: n.name,
            symbolSize: size,
            itemStyle: {
              color: n.category === 0 ? '#409EFF' : '#67C23A'
            },
            draggable: true,
            category: n.category,
            label: {
              show: true,
              position: 'inside',
              align: 'center',
              verticalAlign: 'middle',
              color: '#000000',
              fontSize,
              overflow: 'breakAll'
            }
          }
        })
        const mapRelation = (t) => {
          if (t === 'BELONGS_TO') return '类型'
          if (t === 'FROM_ERA') return '年代'
          return t || ''
        }
        let links = (graphData.value.links || []).map(l => ({
          source: l.source,
          target: l.target,
          label: {
            show: true,
            formatter: mapRelation(l.value)
          }
        }))

        links = links.filter(l => (l.label?.formatter || '') !== 'HAS_IMAGE')

        const usedIds = new Set()
        for (const l of links) {
          usedIds.add(l.source)
          usedIds.add(l.target)
        }
        const idToNode = new Map(nodes.map(n => [n.id, n]))
        const degree = new Map()
        for (const id of usedIds) {
          degree.set(id, (degree.get(id) || 0) + 1)
        }
        const namePrimary = new Map()
        const idAlias = new Map()
        for (const n of nodes) {
          if (n.category === 0) {
            namePrimary.set(n.name || '', n.id)
            continue
          }
          const key = (n.name || '').toLowerCase()
          if (!key) continue
          const primary = namePrimary.get(key) || namePrimary.get(n.name || '')
          if (primary) {
            idAlias.set(n.id, primary)
          } else {
            namePrimary.set(key, n.id)
          }
        }
        if (idAlias.size > 0) {
          links = links.map(l => ({
            ...l,
            source: idAlias.get(l.source) || l.source,
            target: idAlias.get(l.target) || l.target
          }))
        }
        const aliasTargets = new Set(idAlias.keys())
        const finalNodes = nodes.filter(n => !aliasTargets.has(n.id)).filter(n => usedIds.has(n.id))

        // 固定中心节点位置为容器中心
        const mainNode = finalNodes.find(n => n.category === 0)
        if (mainNode && graphContainer.value) {
          const rect = graphContainer.value.getBoundingClientRect()
          mainNode.fixed = true
          mainNode.x = rect.width / 2
          mainNode.y = rect.height / 2
        }

        const option = {
          title: {
            text: '文物知识图谱'
          },
          tooltip: {},
          animationDurationUpdate: 1500,
          animationEasingUpdate: 'quinticInOut',
          series: [{
            type: 'graph',
            layout: 'force',
            roam: true,
            label: { show: true },
            edgeSymbol: ['circle', 'arrow'],
            edgeSymbolSize: [4, 10],
            edgeLabel: { fontSize: 16 },
            data: finalNodes,
            links,
            force: {
              repulsion: 1000,
              gravity: 0.1,
              edgeLength: 200,
              layoutAnimation: true
            }
          }]
        }
        chart.setOption(option, true)
      } else {
        const option = {
          title: { text: '暂无图谱数据' },
          tooltip: {},
          series: [{
            type: 'graph',
            layout: 'force',
            roam: true,
            label: { show: true },
            data: [],
            links: [],
            force: {
              repulsion: 800,
              gravity: 0.1,
              edgeLength: 200,
              layoutAnimation: true
            }
          }]
        }
        chart.setOption(option, true)
      }
    }

    // 监听文物数据变化并拉取知识图谱
    watch(() => props.artifactData, () => {
      fetchGraph()
    })

    onMounted(() => {
      initChart()
      fetchGraph()
      window.addEventListener('resize', () => {
        if (chart) {
          chart.resize()
        }
      })
    })

    return {
      graphContainer
    }
  }
}
</script>

<style scoped>
.knowledge-graph {
  width: 100%;
  height: 100%;
}
</style>
