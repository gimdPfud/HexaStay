/* 유저용 알람 공통 스크립트 */

document.addEventListener('DOMContentLoaded', function () {
    console.log("사용자 주문 상태 알림 스크립트 초기화 시작");

    if (typeof SockJS === 'undefined' || typeof Stomp === 'undefined') {
        console.error("SockJS 또는 Stomp 라이브러리가 로드되지 않았습니다. 알림 기능을 사용할 수 없습니다.");
        return;
    }

    const socket = new SockJS("/ws-order-alert"); // 백엔드 WebSocket 엔드포인트 (관리자용과 동일)
    const stompClient = Stomp.over(socket);
    stompClient.debug = null; // 개발 중에는 console.log로 설정하여 디버그 메시지 확인 가능

    stompClient.connect({}, function (frame) {
        // WebSocket 연결 성공
        console.log("사용자 알림 WebSocket 연결 성공: ", frame);

        // 서버에서 SimpMessagingTemplate.convertAndSendToUser(username, "/queue/order-status", payload) 로 메시지를 보냈습니다.
        // 클라이언트(사용자)는 "/user/queue/order-status"를 구독합니다.
        // Spring은 현재 로그인한 사용자의 세션을 기반으로 이 구독을 올바르게 매칭시켜줍니다.
        const userSpecificQueue = "/user/queue/order-status";

        stompClient.subscribe(userSpecificQueue, function (message) {
            try {
                const notificationData = JSON.parse(message.body); // 백엔드에서 보낸 NotificationPayloadDTO 형태의 JSON
                console.log("새로운 주문 상태 업데이트 알림 수신:", notificationData);

                // --- 여기서부터 수신한 알림을 화면에 표시하는 로직을 구현합니다. ---
                // 예시 1: 간단한 브라우저 alert 창으로 표시
                alert("🔔 주문 알림: " + notificationData.title + "\n" + notificationData.message);

            } catch (error) {
                console.error("사용자: WebSocket 메시지 처리 중 오류 발생:", error);
            }
        });

    }, function (error) {
        // WebSocket 연결 실패
        console.error("사용자 알림 WebSocket 연결 실패:", error);
        // 필요하다면 일정 시간 후 재연결 시도 로직 (선택 사항)
        // setTimeout(connectUserWebSocket, 5000); // 5초 후 재연결 시도
    });

});