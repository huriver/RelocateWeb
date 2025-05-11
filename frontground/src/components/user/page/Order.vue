<script setup>
import { ref, watch } from "vue"
import { useRoute } from "vue-router"
import { getOrderPriceApi } from "@/api/order.js"

const route = useRoute()

const disabled = ref(true)
const form = ref({
  originAddress: "",
  destinationAddress: "",
  numberOfHelpers: 0,
  reservationTime: "",
  notes: ""
})
const rules = ref({
  originAddress: { required: true, message: '请输入起始地点', trigger: 'blur' },
  destinationAddress: { required: true, message: '请输入终止地点', trigger: 'blur' },
  numberOfHelpers: { required: true, message: '请输入工人数量', trigger: 'blur' },
  reservationTime: { required: true, message: '请输入上门时间', trigger: 'blur' },
})

// 禁用日期规则
const disabledDate = (date) => {
  // 计算今天0点的时间戳（去除时分秒影响）
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  // 计算两周后的日期（14天后的23:59:59）
  const twoWeeksLater = new Date(today)
  twoWeeksLater.setDate(today.getDate() + 14)
  twoWeeksLater.setHours(23, 59, 59, 999)

  // 转换比较时间戳
  const time = date.getTime()
  return time < today.getTime() || time > twoWeeksLater.getTime()
}

import { debounce } from 'lodash';

// 定义防抖函数（用户停止操作 500ms 后触发）
const debouncedFetchPrice = debounce(async (formValue) => {
  if (
    formValue.originAddress &&
    formValue.destinationAddress &&
    formValue.numberOfHelpers >= 0
  ) {
    const { data: res } = await getOrderPriceApi({
      serviceId: route.params.id,
      originAddress: formValue.originAddress,
      destinationAddress: formValue.destinationAddress,
      numberOfHelpers: formValue.numberOfHelpers,
    });
    console.log(res);
  }
}, 1000); // 关键：延迟时间设为 500ms

// 监听表单变化
watch(
  () => form.value,
  (newVal) => {
    debouncedFetchPrice(newVal);
  },
  { deep: true } // 深度监听对象属性变化
);
</script>

<template>
  <div class="form">
    <el-form :inline="true" :model="form" :rules="rules">
      <el-form-item label="起始地点" prop="originAddress">
        <el-input v-model="form.originAddress" placeholder="请输入起始地" />
      </el-form-item>
      <el-form-item label="终止地点" prop="destinationAddress">
        <el-input style="width: 220px;" v-model="form.destinationAddress" placeholder="请输入终止地" />
      </el-form-item>
      <el-form-item label="工人数量" prop="numberOfHelpers">
        <el-input-number style="width:198px;" v-model="form.numberOfHelpers" :min="0" :max="10" />
      </el-form-item>
      <el-form-item label="上门时间" prop="reservationTime">
        <el-date-picker v-model="form.reservationTime" type="datetime" :disabled-date="disabledDate"
          placeholder="请选择未来两周内的日期" />
      </el-form-item>
      <el-form-item label="个人备注">
        <el-input v-model="form.notes" style="width: 540px" :rows="10" resize="none" type="textarea"
          placeholder="请您填写备注" />
      </el-form-item>
    </el-form>
  </div>
  <div class="footer">
    <div class="details">
      <div class="left">费用明细</div>
      <div class="right">
        <div>111</div>
        <div>111</div>
        <div>111</div>
        <div>111</div>
        <div>111</div>
      </div>
    </div>
    <div class="pay">
      <div class="left">总计 <span class="price">￥{{ "--" }}</span></div>
      <el-button class="pay-btn" color="#f16622" :disabled="disabled">去支付</el-button>
    </div>
  </div>
</template>

<style scoped lang="less">
.form {
  width: 650px;
  margin: 30px auto;
  box-sizing: border-box;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: absolute;
  z-index: 999;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 110px;
  background-color: rgb(66, 68, 86);
  color: rgb(131, 132, 137);
  box-sizing: border-box;
  padding: 20px;

  .details {
    display: flex;
    align-items: center;

    .left {
      margin-right: 20px;
    }
  }

  .pay {
    display: flex;
    align-items: center;

    .price {
      font-size: 36px;
      color: #f16622;
      margin-right: 20px;
    }

    .pay-btn {
      width: 176px;
      height: 56px;
      border-radius: 28px;
      font-size: 18px;
    }
  }
}
</style>