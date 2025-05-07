document.addEventListener('DOMContentLoaded', function () {
    console.log("사용자 주문 상태 알림 스크립트 초기화 시작");

    if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
        console.error("SockJS 또는 Stomp 라이브러리가 로드되지 않았습니다. 알림 기능을 사용할 수 없습니다.");
        return;
    }

    // Bootstrap Modal 인스턴스를 저장할 변수
    let orderAlertModalInstance = null;
    const modalElement = document.getElementById('orderAlertModal');

    if (modalElement) {
        // 모달 인스턴스 미리 생성 (페이지 로드 시 한 번만)
        orderAlertModalInstance = new bootstrap.Modal(modalElement);
    } else {
        console.error("알림 모달 요소를 찾을 수 없습니다: #orderAlertModal. 모달 알림을 사용할 수 없습니다.");
    }

    const socket = new SockJS("/ws-order-alert");
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
                    // 필요하다면 생성 날짜 등 추가 정보 포함
                    // if (notificationData.createDate) {
                    //    const formattedDate = new Date(notificationData.createDate).toLocaleString('ko-KR');
                    //    bodyHtml += `<p class="text-muted mt-2"><small>발생 시간: ${formattedDate}</small></p>`;
                    // }
                    modalBodyElement.innerHTML = bodyHtml;
                }

                // "확인" 버튼 클릭 이벤트 (모달을 닫는 역할만 한다면, HTML에 data-bs-dismiss="modal"을 추가하는 것이 더 간단합니다)
                // 만약 이미 data-bs-dismiss="modal"이 버튼에 있다면 이 부분은 생략해도 됩니다.
                if (modalConfirmBtn) {
                    modalConfirmBtn.onclick = function() {
                        orderAlertModalInstance.hide();
                        // 여기에 추가적인 작업(예: 알림 읽음 처리 API 호출 등)을 넣을 수 있습니다.
                    };
                }

                // Bootstrap 5에서 모달 헤더 배경색 변경은 직접 스타일링 필요 (bg-warning 클래스가 항상 적용되지 않을 수 있음)
                // 예: modalElement.querySelector('.modal-header').style.backgroundColor = '#ffc107'; // 노란색 계열
                // 또는 CSS로 처리: #orderAlertModal .modal-header { background-color: #ffc107 !important; color: #333 !important; }

                // 모달 표시
                orderAlertModalInstance.show();

            } catch (error) {
                console.error("사용자: WebSocket 메시지 처리 중 오류 발생:", error);
            }
        });

    }, function (error) {
        console.error("사용자 알림 WebSocket 연결 실패:", error);
        // 재연결 로직 (선택 사항)
        // console.log("5초 후 WebSocket 재연결을 시도합니다...");
        // setTimeout(function() {
        //     // connect 함수를 다시 호출하거나, 페이지 새로고침 유도 등
        //     // 여기서는 connect() 함수가 전역에 없으므로, 이 로직을 사용하려면 connect 로직을 별도 함수로 만들어야 합니다.
        // }, 5000);
    });
});