<script setup>
  import { useRouter } from 'vue-router'

  const router = useRouter()
  const props = defineProps({
    title: {
      type: String,
      required: true
    },
    data: {
      type: Array,
      required: true
    },
    routeLink: {
      type: String,
      required: true
    }
  })
  const gotoDetail = (id) => {
    router.push({
      path: `${props.routeLink}/${id}`
    })
  }
</script>

<template>
  <div class="more-card-container" v-show="data.length > 0">
    <div class="head">
      <div class="title">{{ title }}</div>
      <el-button type="primary" style="width: 60px;" @click="router.push(routeLink)">更多</el-button>
    </div>
    <div class="box">
      <div class="data-item" v-for="item in data" :key="item.id" @click="gotoDetail(item.id)">
        <div class="title">{{ item.title }}</div>
        <div class="content">
          {{ item.content }}
        </div>
        <div class="bottom">
          <el-tag class="category" type="primary" v-if="item.category">{{ item.category }}</el-tag>
          <div class="updateTime">{{ item.updateTime }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="less">
  .more-card-container {
    margin-top: 20px; // 顶部外边距稍大，与其他组件区隔更清晰

    .head {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 50px;
      line-height: 50px;
      padding-bottom: 10px; // 增加一点底部内边距，让标题与下方内容有更多呼吸空间
      margin-bottom: 20px; // 标题区域与内容区域的外边距

      .title {
        box-sizing: border-box;
        width: 120px;
        position: relative;
        padding-left: 16px;
        color: #fff;
        background-color: rgb(247, 168, 39); // 保持原始的橙色背景
        font-size: 20px; // 标题字体略微增大，更醒目
        font-weight: bold; // 标题加粗
      }

      .title::after {
        content: "";
        position: absolute;
        right: -20px;
        top: -20px;
        transform: rotate(45deg);
        display: inline-block;
        width: 40px;
        height: 40px;
        background-color: #fff;
      }

      .title::before {
        content: "";
        position: absolute;
        right: -20px;
        bottom: -20px;
        transform: rotate(45deg);
        display: inline-block;
        width: 40px;
        height: 40px;
        background-color: #fff;
      }
    }

    .box {
      padding-top: 10px; // 调整到 10px，因为 head 已经增加了 margin-bottom

      .data-item {
        padding: 18px 20px; // 增大内边距，内容有更多呼吸空间
        margin-bottom: 20px; // 数据项之间的外边距，稍微减小
        cursor: pointer;
        border: 1px solid rgba(0, 0, 0, 0.08); // 边框颜色更柔和
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.03); // 添加轻微的初始阴影，增强立体感
        transition: all 0.3s ease-in-out; // 添加过渡效果，使悬停平滑

        &:hover {
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08); // 悬停时阴影加深
          transform: translateY(-3px); // 悬停时轻微上移
        }

        .title {
          color: var(
            --el-color-primary,
            rgb(64, 158, 255)
          ); // 使用 Element Plus 主色变量，保持一致性
          font-size: 20px; // 标题字体大小略微减小，更均衡
          font-weight: bold; // 加粗
          margin-bottom: 8px; // 标题与内容之间间隔
        }

        .content {
          margin: 10px 0; // 内容上下外边距调整
          display: -webkit-box;
          -webkit-box-orient: vertical;
          -webkit-line-clamp: 3;
          overflow: hidden;
          text-overflow: ellipsis;
          word-break: break-word;
          line-height: 1.6; // 增加行高，提高可读性
          color: #666; // 内容文本颜色更柔和
        }

        .bottom {
          display: flex;
          justify-content: space-between;
          align-items: center; // 垂直居中对齐

          .category {
            // 保持 Element Plus Tag 默认样式，通常已经很简约
            font-size: 12px; // 确保字体大小合适
          }

          .updateTime {
            // 移除 width: 100%，Flexbox 会自动处理对齐
            text-align: right;
            color: #999; // 时间文本颜色更柔和
            font-size: 13px; // 时间文本字体大小
          }
        }
      }

      .data-item:hover .title {
        text-decoration: underline;
        text-decoration-color: var(
          --el-color-primary,
          rgb(64, 158, 255)
        ); // 使用 Element Plus 主色变量
        text-underline-offset: 4px;
      }
    }
  }
</style>