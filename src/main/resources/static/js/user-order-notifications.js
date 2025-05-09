document.addEventListener('DOMContentLoaded', function () {
    console.log("사용자 주문 상태 알림 스크립트 초기화 시작");

    if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
        console.error("알림 기능을 사용할 수 없습니다.");
        return;
    }

    let orderAlertModalInstance = null;
    const modalElement = document.getElementById('orderAlertModal');

    if (modalElement) {
        orderAlertModalInstance = new bootstrap.Modal(modalElement);
    } else {
        console.error("알림 모달 요소를 찾을 수 없습니다: #orderAlertModal. 모달 알림을 사용할 수 없습니다.");
    }

    const ngrokBaseUrl = 'wss://e41c-116-33-138-85.ngrok-free.app'; // 또는 'https://...' 일 수도 있습니다. SockJS는 보통 http/https 기반 URL을 사용합니다.

    const socket = new SockJS("/ws-order-alert"); /*todo : 지우지마 !!!*/
    /*const socket = new SockJS('https://e41c-116-33-138-85.ngrok-free.app/ws-order-alert');*/
    const stompClient = Stomp.over(socket);
    stompClient.debug = null; // 개발 완료 후에는 null로 설정하여 콘솔 로그 최소화

    stompClient.connect({}, function (frame) {
        console.log("사용자 알림 WebSocket 연결 성공: ", frame);
        const userSpecificQueue = "/user/queue/order-status";

        stompClient.subscribe(userSpecificQueue, function (message) {
            try {
                const notificationData = JSON.parse(message.body);
                console.log("새로운 주문 상태 업데이트 알림 수신:", notificationData);

                if (!orderAlertModalInstance) {
                    // 모달 인스턴스가 없다면 기존 alert 방식 사용
                    alert("🔔 주문 알림: " + notificationData.title + "\n" + notificationData.message);
                    return;
                }

                // --- 모달 내용 채우기 및 표시 ---
                const modalTitleElement = modalElement.querySelector('#orderAlertModalLabel');
                const modalBodyElement = modalElement.querySelector('#orderAlertContent');
                const modalConfirmBtn = modalElement.querySelector('#orderAlertConfirmBtn');

                if (modalTitleElement) {
                    // 이모지와 함께 제목 설정
                    modalTitleElement.innerHTML = `🛎️ ${notificationData.title || "새로운 알림"}`;
                }

                if (modalBodyElement) {
                    // 모달 본문 내용 구성 (HTML 사용 가능)
                    let bodyHtml = `<p class="mb-2">${notificationData.message || "주문 상태가 변경되었습니다."}</p>`;
                    if (notificationData.orderId) {
                        bodyHtml += `<p class="text-muted mb-0"><small>주문번호: ${notificationData.orderId}</small></p>`;
                    }
                    if (notificationData.status) {
                        bodyHtml += `<p class="text-muted mb-0"><small>상태: ${notificationData.status}</small></p>`;
                    }
                    modalBodyElement.innerHTML = bodyHtml;
                }


                if (modalConfirmBtn) {
                    modalConfirmBtn.onclick = function() {
                        orderAlertModalInstance.hide();
                    };
                }


                // 모달 표시
                orderAlertModalInstance.show();

            } catch (error) {
                console.error("사용자: WebSocket 메시지 처리 중 오류 발생:", error);
            }
        });

    }, function (error) {
        console.error("사용자 알림 WebSocket 연결 실패:", error);
    });
});