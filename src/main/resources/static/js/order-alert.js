/**
 * 공통 실시간 주문 알림 및 조건부 알림 UI 처리 스크립트
 * - 모든 관련 관리자 페이지에 이 파일 하나만 포함합니다.
 * - 모달 알림은 모달 HTML 요소가 있다면 항상 표시됩니다.
 * - 드롭다운 목록/배지 관련 기능(초기화, 업데이트, 읽음 처리)은
 * 해당 HTML 요소가 페이지에 존재할 때만(주로 /roommenu/list 페이지) 동작합니다.
 */
document.addEventListener('DOMContentLoaded', function () {
    console.log("공통 알림 스크립트 v2 초기화 시작");

    // --- 알림 UI 관련 함수들 ---
    // 이 함수들은 호출될 때 내부적으로 관련 요소 존재 여부를 확인하거나,
    // 호출하는 쪽에서 요소 존재 여부를 확인 후 호출합니다.

    function updateNotificationBadge(count) {
        const badge = document.getElementById('notification-badge');
        if (badge) { // 배지 요소가 있을 때만 업데이트
            const currentCount = parseInt(count) || 0;
            badge.textContent = currentCount;
            badge.style.display = currentCount > 0 ? 'block' : 'none';
            console.log(`(UI) 배지 업데이트: ${currentCount}`);
        }
    }

    function createNotificationItemHTML(notification) {
        const timeValue = notification.createDate || notification.orderTimestamp; // API(createDate) 또는 WS(orderTimestamp)
        const timeString = timeValue ? new Date(timeValue).toLocaleString('ko-KR', { dateStyle: 'short', timeStyle: 'short'}) : '시간 정보 없음';
        const message = `<strong>${notification.memberEmail || '알 수 없음'}</strong>님이 주문<br><span class="text-primary">(${notification.totalPrice ? notification.totalPrice.toLocaleString('ko-KR') : '?'}원 / ${notification.hotelRoomName || '객실 정보 없음'})</span>`;
        const link = notification.orderId ? `/roommenu/adminOrderList?highlight=${notification.orderId}` : '/roommenu/adminOrderList';
        const itemClass = notification.isRead ? 'read' : 'unread';
        return `<li class="${itemClass}"><a class="dropdown-item notification-item" href="${link}" data-notification-id="${notification.notificationId}"><div class="small text-muted">${timeString}</div><div>${message}</div></a></li>`;
    }

    function addNotificationToList(notificationData) {
        const menu = document.getElementById('notification-dropdown-menu');
        if (menu) { // 목록 요소가 있을 때만 추가
            const placeholder = menu.querySelector('p.text-muted');
            if (placeholder) { placeholder.parentElement.remove(); }
            const newItemHTML = createNotificationItemHTML({...notificationData, isRead: false});
            menu.insertAdjacentHTML('afterbegin', newItemHTML);
            console.log(`(UI) 새 알림 목록에 추가: ID ${notificationData.notificationId}`);
            const addedLi = menu.firstChild;
            if (addedLi && addedLi.tagName === 'LI') {
                addedLi.classList.add('unread');
                addedLi.classList.remove('read');
            }
        }
    }

    async function markNotificationsAsReadApiCall(ids) {
        if (!ids || ids.length === 0) return false;
        console.log("(API) 읽음 처리 API 호출 시도:", ids);
        try {
            const response = await fetch('/roommenu/notifications/read', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(ids) });
            if (!response.ok) throw new Error(`HTTP error! ${response.status}`);
            console.log("(API) 알림 읽음 처리 결과:", await response.json());
            return true;
        } catch (error) {
            console.error("(API) 알림 읽음 처리 실패:", error);
            return false;
        }
    }

    // 페이지 로드 시 안 읽은 알림 목록 초기화 (드롭다운 메뉴가 있을 때만 실행)
    async function initializeNotifications() {
        const menu = document.getElementById('notification-dropdown-menu');
        if (!menu) {
            console.log("알림 드롭다운 메뉴(#notification-dropdown-menu) 없음. 목록 초기화 건너뜀.");
            return; // <<--- 드롭다운 메뉴 없으면 여기서 종료!
        }
        console.log("알림 목록 초기화 함수 호출됨 (드롭다운 메뉴 존재)");
        try {
            const response = await fetch('/roommenu/notifications/unread');
            if (!response.ok) throw new Error(`HTTP error! ${response.status}`);
            const data = await response.json();
            console.log("읽지 않은 알림 데이터 수신:", data);
            updateNotificationBadge(data.count || 0); // 배지 업데이트는 항상 시도
            menu.innerHTML = '';
            if (data.notifications && data.notifications.length > 0) {
                data.notifications.forEach(n => menu.insertAdjacentHTML('beforeend', createNotificationItemHTML(n)));
            } else {
                menu.innerHTML = '<li><p class="dropdown-item text-center text-muted small mb-0">새로운 알림이 없습니다.</p></li>';
            }
            console.log("알림 목록 초기화 완료");
        } catch (error) {
            console.error("읽지 않은 알림 로딩 실패:", error);
            menu.innerHTML = '<li><p class="dropdown-item text-center text-danger small mb-0">알림 로딩 실패</p></li>';
            updateNotificationBadge(0);
        }
    }

    // --- 페이지 로드 시 초기화 실행 ---
    initializeNotifications(); // 내부적으로 #notification-dropdown-menu 확인 후 실행됨

    // --- 드롭다운 열림 이벤트 리스너 설정 (드롭다운 버튼 있을 때만) ---
    const dropdownElement = document.getElementById('notificationDropdown');
    if (dropdownElement) {
        console.log("알림 드롭다운(#notificationDropdown) 발견. 'show.bs.dropdown' 이벤트 리스너 설정.");
        dropdownElement.addEventListener('show.bs.dropdown', async function () {
            const menu = document.getElementById('notification-dropdown-menu');
            if (!menu) return;
            const unreadItems = menu.querySelectorAll('li.unread .notification-item');
            const ids = Array.from(unreadItems).map(item => item.getAttribute('data-notification-id')).filter(id => id).map(id => parseInt(id));
            if (ids.length > 0) {
                const success = await markNotificationsAsReadApiCall(ids);
                if (success) {
                    updateNotificationBadge(0);
                    unreadItems.forEach(item => {
                        const li = item.closest('li');
                        if(li) { li.classList.remove('unread'); li.classList.add('read'); }
                    });
                }
            } else {
                updateNotificationBadge(0);
            }
        });
    } else {
        console.log("알림 드롭다운(#notificationDropdown) 없음. 이벤트 리스너 설정 건너뜀.");
    }


    // --- WebSocket 연결 및 처리 (항상 실행 시도) ---
    function connectWebSocket() {
        console.log("WebSocket 연결 시도...");
        const socket = new SockJS("/ws-order-alert"); // 엔드포인트 확인
        const stompClient = Stomp.over(socket);
        stompClient.debug = null; // 디버그 로그 비활성화

        stompClient.connect({}, function (frame) {
            console.log("✅ WebSocket 연결 성공: ", frame);
            stompClient.subscribe("/topic/new-order", function (message) {
                try {
                    const orderData = JSON.parse(message.body);
                    console.log("📦 WebSocket 메시지 수신:", orderData);

                    const alertContent = document.getElementById("orderAlertContent");
                    const confirmBtn = document.getElementById("orderAlertConfirmBtn");
                    const modalElement = document.getElementById("orderAlertModal");

                    if (alertContent && confirmBtn && modalElement) {
                        const content = `
                        <div style="text-align: center; color: #007bff; margin-bottom: 10px;">
                            <i class="bi bi-bell-fill" style="font-size: 2.5rem;"></i>
                        </div>
                        <h6 class="modal-title" style="text-align: center; margin-bottom: 10px;">새로운 룸 서비스 주문 알림</h6>
                        <div style="margin-bottom: 5px;">
                            주문자 : <strong>${orderData.memberEmail || '정보 없음'}</strong><br>
                            총 금액 : <strong>${orderData.totalPrice ? orderData.totalPrice.toLocaleString('ko-KR') : '?'}원</strong><br>
                            주문 객실 : <strong>${orderData.hotelRoomName || '정보 없음'}</strong>  </div>
                        <hr style="margin: 10px 0;">
                        <p style="text-align: center; font-size: 0.9em;">
                            관리자용 페이지를 확인하세요!<br> 확인을 누르시면 관리자용 페이지로 이동합니다.
                        </p>
                    `;
                        // --- 모달 내용 수정 끝 ---

                        alertContent.innerHTML = content; // 수정된 내용 적용

                        let modalInstance = bootstrap.Modal.getInstance(modalElement);
                        if (!modalInstance) { modalInstance = new bootstrap.Modal(modalElement); }
                        modalInstance.show();

                        // 확인 버튼 이벤트 리스너 (중복 방지)
                        const newConfirmBtn = confirmBtn.cloneNode(true);
                        confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
                        newConfirmBtn.addEventListener("click", function () { window.location.href = "/roommenu/adminOrderList"; }, { once: true });
                        console.log("알림 모달 표시 완료 (수정된 디자인 적용)");
                    }

                    // 2. 드롭다운 목록 및 배지 업데이트 (목록/배지 요소가 있을 때만 실행)
                    if (document.getElementById('notification-dropdown-menu') && document.getElementById('notification-badge')) {
                        console.log("알림 목록/배지 요소 발견. UI 업데이트 실행.");
                        addNotificationToList(orderData); // 목록에 추가 시도 (이 함수는 파일 상단에 정의되어 있어야 함)
                        const currentBadge = document.getElementById('notification-badge');
                        let currentCount = 0;
                        if (currentBadge && currentBadge.style.display !== 'none') {
                            currentCount = parseInt(currentBadge.textContent) || 0;
                        }
                        updateNotificationBadge(currentCount + 1); // 배지 업데이트 시도 (이 함수는 파일 상단에 정의되어 있어야 함)
                    }

                } catch (error) {
                    console.error("❌ WebSocket 메시지 처리 오류", error);
                }
            });
        }, function (error) {
            console.error("❌ WebSocket 연결 실패", error);
            // setTimeout(connectWebSocket, 5000); // 필요 시 재연결
        });
    }
    connectWebSocket(); // WebSocket 연결 실행

}); // End DOMContentLoaded