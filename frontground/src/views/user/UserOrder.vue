<template>
  <div class="form-container">
    <el-card class="order-form-card" v-if="isExistingOrderLoading">
      <template #header>
        <div class="card-header-title">加载订单中...</div>
      </template>
      <el-skeleton :rows="5" animated />
    </el-card>

    <el-card class="order-form-card" v-else-if="!showPaymentSection">
      <template #header>
        <div class="card-header-title">填写订单信息</div>
      </template>
      <el-form :model="form" :rules="rules" ref="orderFormRef" label-width="auto">
        <el-form-item label="起始地点" prop="originAddress">
          <el-input v-model="form.originAddress"
                    placeholder="请填写具体地址，例如：xx省xx市xx区xx街道xx小区xx号楼xx单元" />
        </el-form-item>
        <el-form-item label="终止地点" prop="destinationAddress">
          <el-input v-model="form.destinationAddress"
                    placeholder="请填写具体地址，例如：xx省xx市xx区xx街道xx小区xx号楼xx单元" />
        </el-form-item>
        <el-form-item label="工人数量" prop="numberOfHelpers">
          <el-input-number v-model="form.numberOfHelpers" :min="0" :max="10" />
        </el-form-item>
        <el-form-item label="上门时间" prop="reservationTime">
          <el-date-picker v-model="form.reservationTime" type="datetime"
                          :disabled-date="disabledDate" placeholder="请选择未来两周内的日期"
                          value-format="YYYY-MM-DD HH:mm:ss" format="YYYY-MM-DD HH:mm" />
        </el-form-item>
        <el-form-item label="个人备注">
          <el-input v-model="form.notes" :rows="4" resize="none" type="textarea"
                    placeholder="请您填写备注" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="order-confirmation-card"
             v-else-if="showPaymentSection && submittedOrderDetails">
      <template #header>
        <div class="card-header-title">订单详情</div>
      </template>
      <div class="order-summary-content">
        <p>
          订单号: <strong>{{ submittedOrderDetails.orderNumber }}</strong>
        </p>
        <p>
          订单金额: <strong>{{ submittedOrderDetails.orderAmount.toFixed(2) }}元</strong>
        </p>
        <p>下单时间: {{ submittedOrderDetails.orderTime }}</p>
        <p style="margin-top: 20px; color: #606266">请在下方选择支付方式并确认支付。</p>
      </div>
    </el-card>

    <div class="footer">
      <div class="details">
        <div class="left">费用明细</div>
        <div class="right">
          <div v-if="showPaymentSection && submittedOrderDetails">
            <p>
              订单号: <strong>{{ submittedOrderDetails.orderNumber }}</strong>
            </p>
            <p>
              订单金额:
              <strong
                      class="price-value">{{ submittedOrderDetails.orderAmount.toFixed(2) }}元</strong>
            </p>
            <p>下单时间: {{ submittedOrderDetails.orderTime }}</p>
          </div>
          <div v-else-if="estimatedPriceDetails">
            <p>
              预估总价:
              <strong
                      class="price-value">{{ estimatedPriceDetails.estimatedPrice.toFixed(2) }}元</strong>
            </p>
            <p>里程费用: {{ estimatedPriceDetails.mileageCost.toFixed(2) }}元</p>
            <p>工人费用: {{ estimatedPriceDetails.helperCost.toFixed(2) }}元</p>
            <p>服务类型价格乘数: {{ estimatedPriceDetails.categoryPriceMultiplier.toFixed(2) }}</p>
          </div>
          <div v-else>
            <p>请填写地址和工人数量以估算价格。</p>
          </div>
        </div>
      </div>

      <div class="pay">
        <div class="left">
          总计 <span class="price">￥{{ totalPriceDisplay }}</span>
        </div>

        <div v-if="showPaymentSection" class="payment-options-container">
          <div class="payment-timer-info" :style="{ 'text-align': 'right' }">
            <p class="warning-text" v-if="paymentCountdown > 0">
              请在
              <span class="countdown-timer">{{ formattedCountdown }}</span>
              内完成支付，否则订单将自动取消。
            </p>
            <p class="expired-text" v-else>支付时间已过，订单已自动取消。</p>
          </div>

          <div class="payment-actions-wrapper">
            <el-radio-group v-model="selectedPayMethod" class="payment-methods">
              <el-radio :value="1" :disabled="paymentCountdown === 0">微信</el-radio>
              <el-radio :value="2" :disabled="paymentCountdown === 0">支付宝</el-radio>
              <el-radio :value="3" :disabled="paymentCountdown === 0">云闪付</el-radio>
            </el-radio-group>
            <el-button class="pay-btn" color="#f16622" :disabled="payButtonDisabled"
                       @click="handlePayment">
              确认支付
            </el-button>
          </div>
        </div>
        <div v-else>
          <el-button class="pay-btn" color="#f16622" :disabled="disabled" @click="placeOrder">
            立即下单
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  // 引入 onActivated 和 onDeactivated
  import { ref, watch, computed, onUnmounted, onMounted, onActivated, onDeactivated } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  import {
    estimateOrderPriceApi,
    submitOrderApi,
    orderPaymentApi,
    getFrontOrderDetailApi,
  } from '@/api/orderApi.js';
  import { ElMessage, ElMessageBox } from 'element-plus';
  import dayjs from 'dayjs'; // 确保已安装 dayjs: npm install dayjs
  import { debounce } from 'lodash'; // 确保已安装 lodash: npm install lodash

  // 定义组件的 name，以配合 keep-alive 的 include 属性
  import { defineOptions } from 'vue';
  defineOptions({
    name: 'UserOrder',
  });

  const route = useRoute();
  const router = useRouter();

  const orderFormRef = ref(null);
  const disabled = ref(true); // 用于控制“立即下单”按钮的禁用状态
  const form = ref({
    originAddress: '',
    destinationAddress: '',
    numberOfHelpers: 0,
    reservationTime: '',
    notes: '',
  });

  const estimatedPriceDetails = ref(null);

  const submittedOrderDetails = ref(null); // 存储下单成功后的订单详情 或 从历史订单加载的订单详情
  const showPaymentSection = ref(false); // 控制是否显示支付方式选择区域
  const selectedPayMethod = ref(null); // 存储用户选择的支付方式
  const isExistingOrderLoading = ref(false); // 加载现有订单详情时的加载状态

  // --- 倒计时相关状态变量 ---
  const paymentCountdown = ref(0); // 倒计时剩余时间，秒
  let countdownInterval = null; // 存储 interval ID
  const MAX_PAYMENT_DURATION_SECONDS = 15 * 60; // 支付时间限制：15分钟

  // 格式化倒计时显示
  const formattedCountdown = computed(() => {
    const minutes = Math.floor(paymentCountdown.value / 60);
    const seconds = paymentCountdown.value % 60;
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
  });

  // 停止倒计时
  const stopCountdown = () => {
    if (countdownInterval) {
      clearInterval(countdownInterval);
      countdownInterval = null;
      console.log('Countdown stopped.');
    }
  };

  // 开始倒计时 (现在接受一个可选的 initialSeconds 参数)
  const startCountdown = (initialSeconds = MAX_PAYMENT_DURATION_SECONDS) => {
    stopCountdown(); // 先清除旧的计时器，防止重复

    // 如果初始时间小于等于0，直接显示已过期
    if (initialSeconds <= 0) {
      paymentCountdown.value = 0;
      if (submittedOrderDetails.value?.orderNumber) {
        // 这里如果倒计时结束是因为加载进来时就发现过期，则只警告不跳转
        // 如果是运行时倒计时结束，则 request.js 会处理取消逻辑（如果后端有）
        // ElMessage.warning('支付时间已到，订单已自动取消。'); // 由后端返回业务状态并由 request.js 拦截器提示
        // router.replace('/userHome/my-orders'); // 依赖后端返回的订单状态变更为已取消并由轮询或其他机制更新
      }
      return;
    }

    paymentCountdown.value = initialSeconds; // 设置初始倒计时值

    // 立即执行一次倒计时逻辑，避免首次更新延迟1秒
    countdownInterval = setInterval(() => {
      if (paymentCountdown.value > 0) {
        paymentCountdown.value--;
      } else {
        stopCountdown(); // 倒计时结束时停止计时器
        // 倒计时结束的提示和跳转逻辑应该依赖后端的订单状态更新，而不是前端直接判断
        // ElMessage.warning('支付时间已到，订单将自动取消。'); // 由后端返回业务状态并由 request.js 拦截器提示
        // router.replace('/userHome/my-orders'); // 依赖后端返回的订单状态变更为已取消并由轮询或其他机制更新
      }
    }, 1000);

    // 立即执行一次倒计时逻辑
    if (paymentCountdown.value > 0) {
      paymentCountdown.value--;
    } else {
      stopCountdown();
      // 同上，依赖后端和 request.js 的处理
      // ElMessage.warning('支付时间已到，订单将自动取消。');
      // router.replace('/userHome/my-orders');
    }

    console.log(`Countdown started from ${initialSeconds} seconds.`);
  };

  // 提取加载现有订单的逻辑为独立函数
  const loadExistingOrder = async (orderId) => {
    if (isExistingOrderLoading.value) return;
    isExistingOrderLoading.value = true;
    try {
      const { data: res } = await getFrontOrderDetailApi(orderId);
      if (res.code === 1 && res.data) {
        const order = res.data;
        // 只有未支付且状态为 0（待接单/待支付）的订单才显示支付页面并开始倒计时
        if (!order.isPaid && [0].includes(order.orderStatus)) {
          submittedOrderDetails.value = {
            orderNumber: order.orderNumber,
            orderAmount: order.movingPrice, // 假设后端返回的 movingPrice 是订单金额
            orderTime: order.createTime,
            isPaid: order.isPaid,
            orderStatus: order.orderStatus,
          };
          showPaymentSection.value = true;

          // 计算剩余支付时间
          const orderCreateTime = dayjs(order.createTime);
          const now = dayjs();
          const elapsedSeconds = now.diff(orderCreateTime, 'second');
          const remainingSeconds = MAX_PAYMENT_DURATION_SECONDS - elapsedSeconds;

          if (remainingSeconds > 0) {
            paymentCountdown.value = remainingSeconds;
            startCountdown(remainingSeconds); // 立即开始倒计时
            ElMessage.success(
              `成功加载订单 ${order.orderNumber}，请在 ${formattedCountdown.value} 内完成支付。`
            );
          } else {
            // 如果加载时发现已经超时
            paymentCountdown.value = 0;
            ElMessage.warning('此订单的支付时间已过期，请重新下单。');
            // 直接跳转回我的订单列表，因为前端已确认过期且不可支付
            router.replace('/userHome/my-orders');
          }
        } else if (order.isPaid) {
          // 如果已支付
          ElMessage.warning('此订单已支付。');
          router.replace('/userHome/my-orders');
        } else {
          // 其他不可支付的状态
          ElMessage.warning('此订单当前状态无法支付。');
          router.replace('/userHome/my-orders');
        }
      } else {
        // 业务失败 (code !== 1) 或数据不存在，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || '加载订单详情失败，请重试。'); // <-- 移除此行
        console.warn('加载现有订单详情业务失败:', res.msg); // 可以保留日志
        router.replace('/userHome/my-orders'); // 业务失败也跳转
      }
    } catch (error) {
      // 捕获真正的请求错误
      console.error('加载现有订单详情请求失败:', error);
      ElMessage.error('加载订单详情失败，请检查网络或稍后再试。'); // <-- 这个用于网络或HTTP错误
      router.replace('/userHome/my-orders'); // 请求失败也跳转
    } finally {
      isExistingOrderLoading.value = false;
    }
  };

  // onMounted 仅在组件首次挂载时执行一次
  onMounted(async () => {
    console.log('UserOrder component mounted.');
    const existingOrderId = route.query.existingOrderId;
    if (existingOrderId) {
      // 首次挂载时加载现有订单
      await loadExistingOrder(existingOrderId);
    }
  });

  // 当组件被激活（从缓存中重新显示）时，恢复倒计时
  onActivated(async () => {
    console.log('UserOrder component activated.');
    const existingOrderId = route.query.existingOrderId;

    // 如果是现有订单页面，并且 submittedOrderDetails 为空（意味着页面是第一次被激活或被浏览器刷新），
    // 并且有 existingOrderId，则重新加载订单数据。
    // 注意：在 keep-alive 场景下，mounted 不会重复执行，所以 onActivated 承担了判断是否需要初始化加载的职责
    if (existingOrderId && !submittedOrderDetails.value && !isExistingOrderLoading.value) {
      console.log(
        'Existing order ID found on activated, and submittedOrderDetails is null. Reloading order.'
      );
      await loadExistingOrder(existingOrderId);
    }

    // 如果支付 section 应该显示但倒计时未运行且支付时间未到，则启动倒计时
    if (showPaymentSection.value && paymentCountdown.value > 0 && !countdownInterval) {
      console.log('Restarting countdown on activated.');
      startCountdown(paymentCountdown.value); // 从当前剩余时间开始计时
    } else if (
      showPaymentSection.value &&
      paymentCountdown.value <= 0 &&
      !isExistingOrderLoading.value
    ) {
      // 如果已经显示支付区，但倒计时已经为0，则提示过期并跳转（防止用户从历史记录进入过期订单的支付页）
      ElMessage.warning('此订单的支付时间已过期，请重新下单。');
      router.replace('/userHome/my-orders');
    }
  });

  // 当组件被禁用（从缓存中隐藏）时，停止倒计时
  onDeactivated(() => {
    console.log('UserOrder component deactivated.');
    stopCountdown();
  });

  // 当组件卸载时清除计时器，防止内存泄漏
  onUnmounted(() => {
    console.log('UserOrder component unmounted.');
    stopCountdown();
  });
  // --- 倒计时相关状态变量结束 ---

  // 修改 totalPriceDisplay 计算属性，以适应下单前后的显示
  const totalPriceDisplay = computed(() => {
    if (submittedOrderDetails.value) {
      return submittedOrderDetails.value.orderAmount.toFixed(2); // 下单后显示订单金额
    }
    if (estimatedPriceDetails.value) {
      return estimatedPriceDetails.value.estimatedPrice.toFixed(2); // 下单前显示估算金额
    }
    return '--';
  });

  const rules = ref({
    originAddress: {
      required: true,
      message: '请填写具体地址，例如：xx省xx市xx区xx街道xx小区xx号楼xx单元',
      trigger: 'blur',
    },
    destinationAddress: {
      required: true,
      message: '请填写具体地址，例如：xx省xx市xx区xx街道xx小区xx号楼xx单元',
      trigger: 'blur',
    },
    numberOfHelpers: [
      { required: true, message: '请输入工人数量', trigger: 'blur' },
      { type: 'number', min: 0, message: '工人数量不能小于0', trigger: 'change' },
    ],
    reservationTime: { required: true, message: '请选择上门时间', trigger: 'change' },
  });

  const disabledDate = (date) => {
    const today = dayjs().startOf('day');
    // 允许选择未来两周内的日期，包括今天和两周后那天的结束
    const twoWeeksLater = dayjs().add(14, 'day').endOf('day');

    const time = dayjs(date);
    return time.isBefore(today) || time.isAfter(twoWeeksLater);
  };

  const debouncedFetchPrice = debounce(async () => {
    // 只有在非支付流程且表单已加载完毕时才执行估算
    if (!orderFormRef.value || showPaymentSection.value) {
      estimatedPriceDetails.value = null;
      disabled.value = true;
      return;
    }

    const fieldsToValidate = ['originAddress', 'destinationAddress', 'numberOfHelpers'];

    try {
      // 只校验需要估算价格的字段
      const validationResults = await Promise.allSettled(
        fieldsToValidate.map((field) => orderFormRef.value.validateField(field))
      );

      const isValidForEstimate = validationResults.every((result) => result.status === 'fulfilled');

      if (isValidForEstimate) {
        const { data: res } = await estimateOrderPriceApi({
          serviceId: route.params.id,
          originAddress: form.value.originAddress,
          destinationAddress: form.value.destinationAddress,
          numberOfHelpers: form.value.numberOfHelpers,
        });

        if (res.code === 1) {
          estimatedPriceDetails.value = res.data;
          ElMessage.success('价格估算成功！');
          // 价格估算成功后，根据所有表单项的校验结果来决定“立即下单”按钮是否可用
          orderFormRef.value.validate((valid) => {
            disabled.value = !valid || !estimatedPriceDetails.value;
          });
        } else {
          // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
          // let errorMessage = res.msg || '价格估算失败！'; // <-- 移除本地错误消息逻辑
          console.warn('价格估算业务失败:', res.msg); // 可以保留日志
          // if (errorMessage.includes('计算搬家距离发生未知错误')) {
          // 	errorMessage = '您输入的地址无法识别或不完整，请检查后重试。';
          // }
          // ElMessage.error(errorMessage); // <-- 移除此行

          estimatedPriceDetails.value = null;
          disabled.value = true; // 估算失败，禁用下单按钮
        }
      } else {
        // 即使地址或工人数量校验失败，也清空估算结果，并禁用下单按钮
        estimatedPriceDetails.value = null;
        disabled.value = true;
      }
    } catch (error) {
      // 捕获真正的请求错误 (网络问题, CORS, request.js 拦截器抛出错误等)
      console.error('价格估算过程中发生错误:', error);
      // request.js 已经在 rejection 时弹窗了，这里弹出的错误更倾向于网络或未被拦截器详细处理的错误
      ElMessage.error('价格估算过程中发生错误，请稍后再试。'); // <-- 这个用于网络或HTTP错误
      estimatedPriceDetails.value = null;
      disabled.value = true; // 估算失败，禁用下单按钮
    }
  }, 1000);

  const estimationFields = computed(() => ({
    originAddress: form.value.originAddress,
    destinationAddress: form.value.destinationAddress,
    numberOfHelpers: form.value.numberOfHelpers,
  }));

  watch(
    estimationFields,
    () => {
      // 只有当不在支付流程时才触发估算
      if (!showPaymentSection.value) {
        debouncedFetchPrice();
      }
    },
    { deep: true }
  );

  watch(
    () => form.value,
    () => {
      // 监听 form.value 的任何变化，重新校验所有字段并更新下单按钮状态
      if (orderFormRef.value && !showPaymentSection.value) {
        orderFormRef.value.validate((valid) => {
          // 下单按钮可用条件：所有表单项校验通过 且 已经成功获取到估算价格
          disabled.value = !valid || !estimatedPriceDetails.value;
        });
      }
    },
    { deep: true, immediate: true }
  );

  const placeOrder = async () => {
    if (!orderFormRef.value) return;

    orderFormRef.value.validate(async (valid) => {
      if (valid) {
        if (!estimatedPriceDetails.value) {
          ElMessage.warning('请先完成价格估算！');
          // 尝试再次触发估算，以防用户直接点击下单但估算未完成
          debouncedFetchPrice();
          return;
        }

        try {
          const formattedReservationTime = dayjs(form.value.reservationTime).format(
            'YYYY-MM-DD HH:mm:ss'
          );

          const { data: res } = await submitOrderApi({
            serviceId: route.params.id,
            reservationTime: formattedReservationTime,
            movingOrigin: form.value.originAddress,
            movingDestination: form.value.destinationAddress,
            numberOfHelpers: form.value.numberOfHelpers,
            notes: form.value.notes,
          });

          if (res.code === 1) {
            ElMessage.success('订单已创建，请选择支付方式完成支付！');
            submittedOrderDetails.value = res.data; // 存储订单详情
            showPaymentSection.value = true; // 显示支付方式选择区域
            disabled.value = true; // 禁用“立即下单”按钮，因为它不再需要
            startCountdown(MAX_PAYMENT_DURATION_SECONDS); // 订单创建成功后开始倒计时
          } else {
            // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
            // ElMessage.error(res.msg || '下单失败，请稍后再试。'); // <-- 移除此行
            console.warn('下单业务失败:', res.msg); // 可以保留日志
            // 如果下单失败，需要保持在当前页面，并且不进入支付流程
          }
        } catch (error) {
          // 捕获真正的请求错误
          console.error('下单请求失败:', error);
          ElMessage.error('下单失败，请检查网络或联系管理员。'); // <-- 这个用于网络或HTTP错误
          // 如果下单失败，需要保持在当前页面，并且不进入支付流程
        }
      } else {
        // 前端校验失败，错误信息已经由 Element Plus 自动显示
        ElMessage.error('请填写完整的订单信息！'); // 额外的总提示
      }
    });
  };

  // 支付按钮的禁用状态，除了未选择支付方式外，还要检查倒计时是否为0
  const payButtonDisabled = computed(
    () => !selectedPayMethod.value || paymentCountdown.value === 0
  );

  // 处理支付的函数
  const handlePayment = async () => {
    if (!selectedPayMethod.value) {
      ElMessage.warning('请选择支付方式！');
      return;
    }
    if (!submittedOrderDetails.value || !submittedOrderDetails.value.orderNumber) {
      ElMessage.error('订单信息缺失，无法支付。');
      return;
    }
    if (paymentCountdown.value === 0) {
      ElMessage.error('支付时间已过，请重新下单。');
      return;
    }

    try {
      // 弹出确认对话框
      await ElMessageBox.confirm('是否确定支付？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      });

      console.log(
        'Initiating payment for order:',
        submittedOrderDetails.value.orderNumber,
        'with method:',
        selectedPayMethod.value
      );
      const { data: res } = await orderPaymentApi({
        orderNumber: submittedOrderDetails.value.orderNumber,
        payMethod: selectedPayMethod.value,
      });

      if (res.code === 1) {
        // 支付成功，立即显示成功消息
        ElMessage.success('支付成功！');

        stopCountdown(); // 清除订单支付倒计时

        let currentClosingCountdown = 5; // 用于页面关闭倒计时

        // 显示自动关闭提示，并开始倒计时
        const closeMsgInstance = ElMessage({
          message: `支付成功！本页面将在 ${currentClosingCountdown} 秒后自动关闭。`,
          type: 'success',
          duration: 0, // 持续显示，直到倒计时结束或手动关闭
          showClose: true, // 允许用户手动关闭提示
        });

        let closingInterval = setInterval(() => {
          if (currentClosingCountdown > 1) {
            currentClosingCountdown--;
            // 更新提示消息内容
            closeMsgInstance.message = `支付成功！本页面将在 ${currentClosingCountdown} 秒后自动关闭。`;
          } else {
            clearInterval(closingInterval);
            closeMsgInstance.close(); // 关闭倒计时提示

            // 尝试关闭当前标签页
            try {
              window.close();
            } catch (e) {
              console.error('Failed to close window:', e);
              ElMessage.warning('由于浏览器安全限制，无法自动关闭当前页面，请手动关闭此标签页。');
            }
            // 无论标签页是否关闭成功，都跳转到用户主页（或订单列表页）
            router.replace('/userHome/my-orders'); // 使用 replace 防止返回到支付页面
          }
        }, 1000);
      } else {
        // 业务失败 (code !== 1)，request.js 已经弹窗提示了后端 msg
        // ElMessage.error(res.msg || '支付失败，请稍后再试。'); // <-- 移除此行
        console.warn('支付业务失败:', res.msg); // 可以保留日志
      }
    } catch (error) {
      // 用户取消了确认对话框，或者其他支付错误 (网络、HTTP等)
      if (error === 'cancel') {
        ElMessage.info('已取消支付。');
      } else {
        // 捕获真正的请求错误
        console.error('支付过程中发生错误:', error);
        ElMessage.error('支付过程中发生错误，请稍后再试。'); // <-- 这个用于网络或HTTP错误
      }
    }
  };
</script>

<style scoped lang="less">
  .form-container {
    display: flex;
    flex-direction: column;
    min-height: calc(100vh - 60px); /* Adjust based on header height */
    padding-bottom: 150px; /* 调整 padding-bottom 以适应新的 footer 高度 */
  }

  .order-form-card {
    width: 600px; /* Adjust width of the card */
    margin: 30px auto; /* Center the card */
    border-radius: 12px; /* Softer corners */
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08); /* Lighter, modern shadow */
    background-color: #fff; /* Ensure white background for clarity */
    padding: 20px 30px; /* Added internal padding for the card content */

    :deep(.el-card__header) {
      border-bottom: none; /* Remove default header border */
      padding: 0; /* Remove default header padding */
      margin-bottom: 20px; /* Add space below header */
    }

    .card-header-title {
      font-size: 26px; /* Larger, more prominent title */
      font-weight: bold;
      text-align: center;
      color: #333;
    }

    :deep(.el-form-item) {
      margin-bottom: 20px; /* Consistent spacing */
      display: flex; /* Make form items full width */
      align-items: center; /* Vertically center label and input */

      .el-form-item__label {
        flex-shrink: 0; /* Prevent label from shrinking */
        width: 100px !important; /* Fixed label width for alignment (adjust as needed) */
        text-align: right;
        padding-right: 12px;
        font-weight: 500; /* Slightly bolder labels */
        color: #555;
      }

      .el-form-item__content {
        flex-grow: 1;
        flex-basis: 0; /* Allow content to fill remaining space */
        .el-input,
        .el-input-number,
        .el-date-editor {
          width: 100%; /* Make inputs fill available width */
        }
      }
    }

    .el-textarea {
      .el-textarea__inner {
        min-height: 100px !important; /* Ensure textarea is tall enough */
      }
    }
  }

  /* --- 新增：订单确认卡片的样式 --- */
  .order-confirmation-card {
    width: 600px; /* 与表单卡片保持一致的宽度 */
    margin: 30px auto; /* 居中显示 */
    border-radius: 12px;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.08);
    background-color: #fff;
    padding: 20px 30px;

    :deep(.el-card__header) {
      border-bottom: none;
      padding: 0;
      margin-bottom: 20px;
    }

    .card-header-title {
      font-size: 26px;
      font-weight: bold;
      text-align: center;
      color: #333;
    }

    .order-summary-content {
      text-align: center; /* 内容居中 */
      padding: 20px;
      font-size: 16px;
      color: #333;

      p {
        margin-bottom: 10px; /* 段落间距 */
      }

      strong {
        color: #f16622; /* 订单号和金额突出显示 */
      }
    }
  }
  /* --- 订单确认卡片样式结束 --- */

  .footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: fixed;
    z-index: 999;
    left: 0;
    bottom: 0;
    width: 100%;
    height: 150px; /* 增加 footer 高度以容纳新内容 */
    background-color: #2c3e50; /* A darker, softer blue-grey */
    color: #e0e0e0; /* Lighter text for contrast */
    box-sizing: border-box;
    padding: 0 80px; /* Slightly more padding */
    box-shadow: 0 -4px 15px rgba(0, 0, 0, 0.15); /* More pronounced but soft shadow */

    .details {
      display: flex;
      flex-direction: column; /* Stack left and right parts */
      align-items: flex-start;
      font-size: 14px;

      .left {
        margin-bottom: 10px; /* Space between title and details */
        color: #fff;
        font-weight: bold;
        font-size: 18px; /* Slightly larger title */
      }

      .right {
        p {
          margin-bottom: 8px; /* 增加行间距，提供更多呼吸空间 */
          line-height: 1.4;
          color: #b0b0b0; /* Softer color for detail text */
          font-size: 13px;
        }
        .price-value {
          color: #f16622;
          font-weight: bold;
          font-size: 16px;
        }
      }
    }

    .pay {
      display: flex;
      align-items: center; // 确保 '总计' 和支付选项块垂直居中对齐

      .payment-options-container {
        display: flex;
        flex-direction: column; // 垂直堆叠：倒计时信息 和 支付选项/按钮
        align-items: flex-end; // 整个 payment-options-container 内部的内容右对齐
        gap: 10px; // 倒计时信息和下方支付方式的间距

        .payment-timer-info {
          font-size: 14px;
          .warning-text {
            color: #ffc107; // 警告色
            font-weight: bold;
            .countdown-timer {
              font-size: 16px;
              color: #ff5722; // 倒计时数字更突出
              font-weight: bolder;
            }
          }
          .expired-text {
            color: #dc3545; // 过期提示色
            font-weight: bold;
          }
        }

        .payment-actions-wrapper {
          display: flex; // 恢复支付方式和按钮的水平布局
          align-items: center; // 垂直居中对齐
          gap: 20px; /* Spacing between radio group and button */
        }

        .payment-methods {
          .el-radio {
            color: #e0e0e0; /* Match footer text color */
            margin-right: 15px; /* Spacing between radio buttons */
          }
          /* You might need deep selectors if default styles are stubborn */
          :deep(.el-radio__input.is-checked + .el-radio__label) {
            color: #f16622; /* Highlight selected option */
          }
          :deep(.el-radio__label) {
            color: #e0e0e0;
          }
        }
      }

      .pay-btn {
        width: 200px; /* Slightly wider button */
        height: 60px; /* Slightly taller button */
        border-radius: 30px; /* More rounded */
        font-size: 20px; /* Larger text on button */
        font-weight: bold;
        transition: all 0.3s ease;
        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 10px rgba(241, 102, 34, 0.4);
        }
      }

      .left {
        margin-right: 30px; /* Space between total text and button */
        font-size: 18px; /* Slightly larger "总计" text */
        color: #fff;
      }

      .price {
        font-size: 40px; /* Even larger total price */
        color: #f16622;
        font-weight: bold;
      }
    }
  }
</style>
