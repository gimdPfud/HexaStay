// (추가) static/js/review.js
document.addEventListener("DOMContentLoaded", function () {
    const likeButtons = document.querySelectorAll(".like-btn");

    likeButtons.forEach(button => {
        button.addEventListener("click", () => {
            const reviewNo = button.getAttribute("data-review-id");

            fetch("/reviewLike/toggle", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ reviewNo })
            })
                .then(res => res.json())
                .then(data => {
                    if (data.status === "ok") {
                        // 좋아요 수 갱신
                        button.querySelector("span").textContent = data.likeCount;
                    } else if (data.status === "duplicated") {
                        alert("이미 좋아요를 누르셨습니다.");
                    } else if (data.status === "unauthorized") {
                        alert("로그인이 필요합니다.");
                    }
                })
                .catch(err => {
                    console.error("좋아요 요청 실패:", err);
                });
        });
    });
});

