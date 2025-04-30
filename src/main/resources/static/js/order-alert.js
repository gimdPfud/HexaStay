document.addEventListener("DOMContentLoaded", function () {
    const socket = new SockJS("/ws-order-alert");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("✅ 웹소켓 연결됨: ", frame);

        stompClient.subscribe("/topic/new-order", function (message) {
            try {
                const orderData = JSON.parse(message.body);
                console.log("📦 메시지 수신:", orderData);

                const content = `
                    주문자: <strong>${orderData.memberEmail}</strong><br>
                    총 금액: <strong>${orderData.totalPrice}원</strong><br>
                    관리자용 페이지를 확인하세요! 확인을 누르시면 관리자용 페이지로 갑니다.
                `;
                const alertContent = document.getElementById("orderAlertContent");
                const confirmBtn = document.getElementById("orderAlertConfirmBtn");

                // 현재 페이지에 모달이 있는 경우만 실행
                if (alertContent && confirmBtn) {
                    alertContent.innerHTML = content;

                    const modal = new bootstrap.Modal(document.getElementById("orderAlertModal"));
                    modal.show();

                    confirmBtn.addEventListener("click", function () {
                        window.location.href = "/roommenu/adminOrderList";
                    });
                }

            } catch (error) {
                console.error("❌ 메시지 처리 오류", error);
            }
        });

    }, function (error) {
        console.error("❌ 웹소켓 연결 실패", error);
    });
});