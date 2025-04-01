const commCategory = document.getElementById('commCategory').value;
const commContentNum = document.getElementById('commContentNum').value;

let currentPage = 1;

function loadComments(page) {

    if(page < 1) return;
    currentPage = page;

    // GET 요청
    fetch(`/comment?commCategory=${commCategory}&commContentNum=${commContentNum}&page=${page}`)
        .then(response => response.json())
        .then(data => {

            renderComments(data.content);
            renderPage(data.totalPages);
        })
        .catch(error => console.error("댓글 로드 에러:", error));
}
function renderComments(comments) {

    const container = document.getElementById('restCommList');

    const html = comments.map(comment => {

        return `
      <div class="comment" data-commnum="${comment.commNum}" style="margin-bottom: 1.5rem;">
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div>
            <span style="font-weight: bold;">작성자: ${comment.memberName ? comment.memberName : comment.memberNum}</span>
            <span style="font-size: 0.8rem; color: #999; margin-left: 1rem;">${comment.createdAt}</span>
          </div>
          <div style="display: flex; gap: 0.5rem;">
            <button class="modify-btn" style="background-color: #ffcc00; border: none; padding: 5px 10px; border-radius: 6px; font-size: 0.9rem; cursor: pointer;">수정</button>
            <button class="delete-btn" style="background-color: #ff3b30; border: none; color: #fff; padding: 5px 10px; border-radius: 6px; font-size: 0.9rem; cursor: pointer;">삭제</button>
          </div>
        </div>
        <div class="comment-content" style="margin-top: 0.5rem; font-size: 1rem; line-height: 1.4;">
          ${comment.commContent}
        </div>
      </div>
      `;

    }).join('');
    container.innerHTML = html;
    btnEvent();
}


function renderPage(totalPages) {
    let paginationHTML = '';
    if(currentPage > 1) {
        paginationHTML += `<button id="prevPage" style="margin-right: 10px;">&lt; 이전</button>`;
    }
    paginationHTML += `<span>페이지 ${currentPage} / ${totalPages}</span>`;
    if(currentPage < totalPages) {
        paginationHTML += `<button id="nextPage" style="margin-left: 10px;">다음 &gt;</button>`;
    }
    let paginationContainer = document.getElementById('paginationContainer');
    if(!paginationContainer) {

        paginationContainer = document.createElement('div');
        paginationContainer.id = "paginationContainer";
        paginationContainer.style.marginTop = "1rem";
        document.getElementById('restCommList').appendChild(paginationContainer);
    }
    paginationContainer.innerHTML = paginationHTML;

    const prevBtn = document.getElementById('prevPage');
    const nextBtn = document.getElementById('nextPage');
    if(prevBtn) {
        prevBtn.addEventListener('click', () => loadComments(currentPage - 1));
    }
    if(nextBtn) {
        nextBtn.addEventListener('click', () => loadComments(currentPage + 1));
    }
}

function btnEvent() {
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            const commentElem = this.closest('.comment');
            const commNum = commentElem.getAttribute('data-commnum');
            deleteComment(commNum);
        });
    });

    document.querySelectorAll('.modify-btn').forEach(button => {
        button.addEventListener('click', function() {
            const commentElem = this.closest('.comment');
            const commNum = commentElem.getAttribute('data-commnum');
            modifyComment(commNum, commentElem);
        });
    });
}

document.getElementById('submitComm').addEventListener('click', function() {
    const memberNum = document.getElementById('memberNum').value;
    const commContent = document.getElementById('commContent').value;
    const commCategory = document.getElementById('commCategory').value;
    const commContentNum = document.getElementById('commContentNum').value;

    const submitJson = {
        commCategory: Number(commCategory),
        commContent: commContent,
        commContentNum: commContentNum,
        memberNum: memberNum,
    };

    fetch('/comment/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(submitJson)
    })

        .then(result => {
            loadComments(currentPage);
            document.getElementById('commContent').value = '';
        })
        .catch(error => console.error("댓글 작성 에러:", error));
    loadComments(currentPage);
});

function deleteComment(commNum) {
    if (confirm("정말로 삭제하시겠습니까?")) {
        fetch(`/comment/delete/${commNum}`, {
            method: 'DELETE'
        })
            .then(response => {
                if (response.ok) {
                    loadComments(currentPage);
                } else {
                    alert("댓글 삭제 실패");
                    loadComments(currentPage);
                }
            })
            .catch(error => console.error("댓글 삭제 에러:", error));
        loadComments(currentPage);
    }
}


function modifyComment(commNum, commentElem) {
    const contentDiv = commentElem.querySelector('.comment-content');
    const oldContent = contentDiv.innerText;

    const input = document.createElement('input');
    input.type = 'text';
    input.value = oldContent;
    input.style.width = '80%';

    contentDiv.innerHTML = '';
    contentDiv.appendChild(input);

    const saveBtn = document.createElement('button');
    saveBtn.innerText = '저장';
    saveBtn.style.marginLeft = '10px';
    contentDiv.appendChild(saveBtn);

    saveBtn.addEventListener('click', function() {
        const newContent = input.value;
        fetch(`/comment/${commNum}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ commContent: newContent })
        })
            .then(response => {
                if (response.ok) {
                    loadComments(currentPage);
                } else {
                    alert("댓글 수정 실패");
                }
            })

            .catch(error => console.error("댓글 수정 에러:", error));
    });
}

loadComments(currentPage);