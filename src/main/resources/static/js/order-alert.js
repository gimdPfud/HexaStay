
document.addEventListener("DOMContentLoaded", function () {
    const socket = new SockJS("/ws-order-alert");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("✅ 웹소켓 연결됨: ", frame);

        stompClient.subscribe("/topic/new-order", function (message) {
            try {
                const orderData = JSON.parse(message.body);
                console.log("📦 메시지 수신:", orderData);

                // --- 기존 모달 로직 시작 ---
                const alertContent = document.getElementById("orderAlertContent");
                const confirmBtn = document.getElementById("orderAlertConfirmBtn");

                if (alertContent && confirmBtn) {
                    const content = `
                        <div style="text-align: center; color: #007bff; margin-bottom: 10px;">
                            <i class="bi bi-bell-fill" style="font-size: 2.5rem;"></i> </div>
                        <h6 class="modal-title" style="text-align: center; margin-bottom: 10px;">새로운 객실 서비스 주문 알림</h6> <div style="margin-bottom: 5px;">
                            주문자 : <strong>${orderData.memberEmail}</strong><br> 총 금액 : <strong>${orderData.totalPrice}원</strong> 주문 호텔 : <strong>${orderData.hotelRoomName || '정보 없음'}</strong></div>
                        <hr style="margin: 10px 0;"> <p style="text-align: center; font-size: 0.9em;">객실 서비스 관리자 페이지(주문내역)를 확인하세요!<br>
                            확인을 누르시면 주문 내역 페이지로 이동합니다.
                        </p>
                        `;

                    alertContent.innerHTML = content;

                    const modalElement = document.getElementById("orderAlertModal");
                    // Bootstrap 5 모달 인스턴스 가져오기/생성
                    let modalInstance = bootstrap.Modal.getInstance(modalElement);
                    if (!modalInstance) {
                        modalInstance = new bootstrap.Modal(modalElement);
                    }
                    modalInstance.show();

                    // 이벤트 리스너 중복 방지 (기존 코드가 잘 동작했다면 유지, 아니면 아래 방식으로 개선)
                    const newConfirmBtn = confirmBtn.cloneNode(true);
                    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
                    newConfirmBtn.addEventListener("click", function () {
                        window.location.href = "/roommenu/adminOrderList"; // 또는 orderData.orderId를 이용한 링크
                    }, { once: true }); // 한번만 실행되도록 옵션 추가

                }
                // --- 기존 모달 로직 끝 ---


                // --- roommenu/list 페이지 추가 알림 로직 시작 ---
                if (window.location.pathname.includes('/roommenu/list')) {
                    console.log("룸서비스 목록 페이지 알림 처리");

                    // 전역 함수 또는 해당 페이지 스코프의 함수 호출
                    // (위 list.html의 script 블록에 정의된 함수 사용)
                    if (typeof addNotificationToList === 'function' && typeof updateNotificationBadge === 'function') {
                        addNotificationToList(orderData); // 목록에 추가

                        // 배지 숫자 업데이트
                        const currentBadge = document.getElementById('notification-badge');
                        let currentCount = 0;
                        if (currentBadge && currentBadge.style.display !== 'none') {
                            currentCount = parseInt(currentBadge.textContent) || 0;
                        }
                        updateNotificationBadge(currentCount + 1);

                    } else {
                        console.warn("알림 처리 함수(addNotificationToList, updateNotificationBadge)를 찾을 수 없습니다.");
                    }
                }
                // --- roommenu/list 페이지 추가 알림 로직 끝 ---

            } catch (error) {
                console.error("❌ 메시지 처리 오류", error);
            }
        });

    }, function (error) {
        console.error("❌ 웹소켓 연결 실패", error);
        // 연결 실패 시 재시도 로직 추가 고려
    });
});