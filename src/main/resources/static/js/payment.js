// 결제 금액 설정
const amount = 50000;

// UUID를 사용한 주문 ID 생성 함수
function generateOrderId() {
    return 'order_' + ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
    );
}

// 토스 결제 초기화 및 결제 위젯 렌더링
const clientKey = 'test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm';
const paymentWidget = PaymentWidget(clientKey, PaymentWidget.ANONYMOUS);

paymentWidget.renderPaymentMethods('#payment-method', { value: amount });
paymentWidget.renderAgreement('#agreement');

// 결제 요청
document.getElementById('payment-request-button').addEventListener('click', function() {
    const orderId = generateOrderId();
    // 결제 수단 선택 후 바로 성공 페이지로 이동
    window.location.href = window.location.origin + '/toss/success?paymentKey=MOCK_PAY_' + orderId + '&orderId=' + orderId + '&amount=' + amount;
});

function generateRandomString() {
    return Math.random().toString(36).substring(2, 12);
}
 