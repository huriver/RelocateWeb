<script setup>
import { queryServiceApi, getServiceDetailApi } from '@/api/service.js'
import { getServiceRatingApi } from '@/api/rating.js'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = ref({
  page: 0,
  pageSize: 5,
  categoryId: "",
  total: 0
})
const data = ref([])
const loading = ref(false)
const noMore = ref(false)

const queryService = async () => {
  if (noMore.value || loading.value) return

  loading.value = true
  try {
    const { data: res } = await queryServiceApi({
      ...form.value,
      page: form.value.page + 1 // 先使用+1的页码查询
    })

    if (res.code !== 1) return ElMessage.error(res.msg)

    // 更新页码和总数
    form.value.page += 1
    form.value.total = res.data.total

    // 追加数据
    data.value.push(...res.data.records)

    // 判断是否还有更多数据
    noMore.value = data.value.length >= res.data.total
  } catch {
    loading.value = false
    noMore.value = true
  } finally {
    loading.value = false
  }
}

// 新增展开状态管理
const expandStates = ref({})
// 新增切换详情方法
const toggleDetail = (id) => {
  expandStates.value = {
    ...expandStates.value,
    [id]: !expandStates.value[id]
  }
}

const detailData = ref({})
const detailLoading = ref({})
// 修改后的详情方法
const getDetail = async (id) => {
  toggleDetail(id)
  if (detailData.value[id]) return
  detailLoading.value[id] = true
  const { data: res } = await getServiceDetailApi(id)
  if (res.code !== 1) return ElMessage.error(res.msg)
  detailData.value[id] = res.data
  // 加载评价
  const { data: rating } = await getServiceRatingApi(id)
  // 只要展示一条，其余的可以跳转
  detailData.value[id].rating = rating.data[0]
  detailLoading.value[id] = false
}
const rateColors = ref(['#99A9BF', '#F7BA2A', '#FF9900'])

const selectService = (id) => {
  const route = router.resolve({ path: `/order/${id}` })
  window.open(route.href, '_blank')
}
</script>

<template>
  <div class="service-container home-container">
    <ul v-infinite-scroll="queryService" :infinite-scroll-distance="0" :infinite-scroll-disabled="noMore || loading"
      class="infinite-list">
      <li v-for="item in data" :key="item.id">
        <div class="title">
          <h1 class="name">{{ item.serviceName }}</h1>
          <el-tag class="category">{{ item.categoryName }}</el-tag>
        </div>
        <div class="description">{{ item.shortDescription }}</div>
        <div class="price-car">
          <div class="truckType-name">{{ item.truckTypeName }}</div>
          <div class="base-price">起步价（5公里以内）：{{ item.basePrice }}元</div>
          <el-button type="success" link @click="getDetail(item.id)">{{ expandStates[item.id] ? '收起详情' : '查询详情'
          }}</el-button>
        </div>

        <!-- 新增详情内容 -->
        <transition name="slide">
          <div v-if="expandStates[item.id]" class="detail-content">
            <div v-if="!detailLoading[item.id]">
              <div class="detail-description description-item">
                <div class="head-title">装载能力</div>
                <div class="box">{{ detailData[item.id].loadingCapacityDescription }}</div>
              </div>
              <div class="car-description description-item">
                <div class="head-title">货车信息</div>
                <div class="box">
                  <div class="box-item">
                    <div class="label">货车描述：</div>
                    <div class="info">{{ detailData[item.id].truckType.description }}</div>
                  </div>
                  <div class="box-item">
                    <div class="label">货车规格：</div>
                    <div class="info">{{ detailData[item.id].truckType.capacity }}</div>
                  </div>
                  <div class="box-item">
                    <div class="label">起步价（5公里以内）：</div>
                    <div class="info">{{ detailData[item.id].truckType.baseFare }}元</div>
                  </div>
                  <div class="box-item">
                    <div class="label">5-25公里每公里价格：</div>
                    <div class="info">{{ detailData[item.id].truckType.pricePerKmTier1 }}元/公里</div>
                  </div>
                  <div class="box-item">
                    <div class="label">25-30公里每公里价格：</div>
                    <div class="info">{{ detailData[item.id].truckType.pricePerKmTier2 }}元/公里</div>
                  </div>
                  <div class="box-item">
                    <div class="label">30-50公里每公里价格：</div>
                    <div class="info">{{ detailData[item.id].truckType.pricePerKmTier3 }}元/公里</div>
                  </div>
                  <div class="box-item">
                    <div class="label">50-80公里每公里价格：</div>
                    <div class="info">{{ detailData[item.id].truckType.pricePerKmTier4 }}元/公里</div>
                  </div>
                  <div class="box-item">
                    <div class="label">超过80公里每公里价格：</div>
                    <div class="info">{{ detailData[item.id].truckType.pricePerKmTier5 }}元/公里</div>
                  </div>
                  <div class="box-item">
                    <div class="label">价格乘数：</div>
                    <div class="info">{{ detailData[item.id].categoryPriceMultiplier }}</div>
                  </div>
                </div>
              </div>
              <div class="detail-description description-item">
                <div class="head-title">费用</div>
                <div class="box-item">
                  <div class="label">工人费用：</div>
                  <div class="info">{{ detailData[item.id].perHelperCost }}元/人</div>
                </div>
                <div class="box-item">
                  <div class="label">费用计算：</div>
                  <div class="info">（公里数 * 公里费用 + 工人数 * 工人费用）* 价格乘数</div>
                </div>
              </div>
              <div class="rate-description description-item">
                <div class="head-title">服务评价</div>
                <div class="box">
                  <div class="box-item">
                    <div class="label">评论：</div>
                    <div class="info">{{ detailData[item.id].rating?.comment || "暂无评论" }}</div>
                  </div>
                  <div class="box-item">
                    <div class="label">评分值：</div>
                    <el-rate v-model="detailData[item.id].averageRating" :colors="rateColors" disabled show-score
                      text-color="#ff9900" score-template="{value}" />
                  </div>
                  <el-button link style="font-size: 12px;padding: 0;" type="primary">查看更多评论</el-button>
                </div>
              </div>
              <el-button link type="danger" style="font-size: 20px; float: right;"
                @click="selectService(item.id)">选择服务</el-button>
            </div>
            <div v-else style="text-align: center;height: 80px;line-height: 80px;">加载中...</div>
          </div>
        </transition>
      </li>

      <!-- 加载状态提示 -->
      <li v-if="loading" class="loading-text">加载中...</li>
      <li v-if="noMore" class="no-more-text">已经到底啦~</li>
    </ul>
  </div>
</template>

<style scoped lang="less">
.service-container {
  height: 100%;

  .infinite-list {
    height: 100%;
    overflow: auto;
    padding: 0;
    margin: 0;

    li {
      position: relative;
      overflow: hidden;
      list-style: none;
      padding: 16px;
      border-bottom: 1px solid #eee;
      background: white;
      transition: 0.3s;

      &:hover {
        background: #f8f9fa;
      }

      &:last-child {
        border-bottom: none;
      }

      .title {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .description {
        color: #666;
        margin: 10px 0;
      }

      .price-car {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 14px;

        .base-price {
          color: #e6a23c;
          font-weight: 500;
        }
      }

      .detail-content {
        margin-top: 16px;
        padding-top: 16px;
        border-top: 1px dashed #ccc;
        transform: scaleY(1);
        transform-origin: top;
        opacity: 1;
        transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
        will-change: transform, opacity;
        font-size: 12px;
        color: #666;

        .detail-item {
          margin-bottom: 12px;

          label {
            color: #666;
            font-weight: 500;
            margin-right: 8px;
          }

          p {
            display: inline;
            color: #888;
          }
        }

        .head-title {
          color: #000;
          font-size: 14px;
          margin-bottom: 10px;
        }

        .description-item {
          margin-bottom: 20px;
        }

        .box-item {
          display: flex;
          align-items: center;
        }
      }

      .slide-enter-active,
      .slide-leave-active {
        transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
      }

      .slide-enter-from {
        transform: scaleY(0.95);
        opacity: 0;
      }

      .slide-leave-to {
        transform: scaleY(0.95);
        opacity: 0;
      }
    }
  }

  .loading-text,
  .no-more-text {
    text-align: center;
    color: #888;
    padding: 16px;
  }
}
</style>