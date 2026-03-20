const replySessionEL = document.querySelector('div.reply-session');

// const cp = replySessionEL ? (replySessionEL.getAttribute('data-contextPath') || '').replace(/\/$/, '') : '';
const num = replySessionEL?.getAttribute('data-num');
const postsUrl = replySessionEL?.getAttribute('data-postsUrl');

// jquery : $.ajax()
function initReplySystem() {
	if(! postsUrl || ! num) {
		alert('댓글 기능이 비활성화되어 있습니다.');
		return false;
	}
	
	return true;
}

// 페이징 처리
$(function(){
	loadContent(1);
});

function loadContent(page) {
	if(! initReplySystem()) {
		return;
	}
	
	const url = `${postsUrl}/${num}`;
	const params = {pageNo:page};
	
	const fn = function(data) {
		addNewReply(data);
	};
	
	ajaxRequest(url, 'get', params, 'json', fn);
}

function addNewReply(data) {
	const listReply = data.listReply;
	const totalCount = Number(data.totalCount) || 0;
	const pageNo = Number(data.pageNo) || 0;
	const totalPage = Number(data.totalPage) || 0;
	const paging = data.paging;
	
	const htmlText = renderReplies(listReply, pageNo);
	
	$('#listReply .reply-count').html(`댓글 ${totalCount}개`);
	$('#listReply .reply-page').html(`[목록, ${pageNo}/${totalPage} 페이지]`);
	
	$('#listReply .list-content').attr('data-pageNo', pageNo);
	$('#listReply .list-content').attr('data-totalPage', totalPage);
	
	if(totalCount === 0) {
		$('#listReply').hide();
		$('#listReply .list-content').empty();
		
		return;
	} 
	
	$('#listReply').show();
	$('#listReply .list-content').html(htmlText);
	$('#listReply .page-navigation').html(paging);
}

// 댓글 등록
$(function(){
	$('button.btnSendReply').click(function(){
		if(! initReplySystem()) {
			return;
		}
				
		const $div = $(this).closest('div.reply-form');

		let content = $div.find('textarea').val().trim();
		if(! content) {
			$div.find('textarea').focus();
			return false;
		}
		
		const url = `${postsUrl}/insert`;
		// const params = 'num=' + num + '&content=' + encodeURIComponent(content) + '&parentNum=0';
		const params = {num:num, content:content, parentNum:0}; // 객체로 전송하면 인코딩하면 안됨
		
		const fn = function(data){
			$div.find('textarea').val('');
			loadContent(1);
		};
		
		ajaxRequest(url, 'post', params, 'json', fn);
	});
});

// 삭제, 신고 메뉴
$(function(){
	$('div#listReply').on('click', '.dropdown-button', function(){
		const $menu = $(this).next('.reply-menu');
		
		$('.reply-menu').not($menu).addClass('d-none');
		
		$menu.toggleClass('d-none');
	});
	
	$('body').on('click', function(evt) {
		const parent = evt.target.parentNode;
		const isMatch = parent.tagName === 'SPAN' && $(parent).hasClass('dropdown-button');		
		
		if(isMatch) {
			return false;
		}
		/*
		if($(parent).hasClass('dropdown-button')) {
			return false;
		}
		*/
		
		$('div.reply-menu:not(.d-none)').addClass('d-none');
	});
});

// 댓글 삭제
$(function(){
	$('div#listReply').on('click', '.deleteReply', function(){
		if(! initReplySystem()) {
			return;
		}
						
		if(! confirm('게시물을 삭제하시겠습니까 ? ')) {
		    return false;
		}
		
		let replyNum = $(this).attr('data-replyNum');
		let page = $(this).attr('data-pageNo');
		
		const url = `${postsUrl}/${replyNum}`;
		const params = {mode:'reply'};
		
		const fn = function(data){
			loadContent(page);
		};
		
		ajaxRequest(url, 'delete', params, 'json', fn);
	});
});

// 댓글 좋아요 / 싫어요
$(function(){
	// 댓글 좋아요 / 싫어요 등록
	$('div#listReply').on('click', 'button.btnSendReplyLike', function(){
		if(! initReplySystem()) {
			return;
		}
						
		const $btn = $(this);
		
		let isUserLiked = $btn.parent('.reply-like-item').attr('data-userLiked') !== '-1';
		if(isUserLiked) {
			alert('게시글 공감여부가 등록되어 있습니다.');
			return false;
		}
		
		let replyNum = $btn.attr('data-replyNum');
		let replyLike = $btn.attr('data-replyLike');
		
		let msg = '게시글이 마음에 들지 않으십니까 ?';
		if(replyLike === '1') {
			msg = '게시글에 공감하십니까 ?';
		}
		
		if(! confirm(msg)) {
			return false;
		}
		
		const url = `${postsUrl}/like`;
		const params = {replyNum:replyNum, replyLike:replyLike};

		const fn = function(data){
			let state = data.state;
			
			if(state === 'true') {
				let likeCount = data.likeCount;
				let disLikeCount = data.disLikeCount;
				
				$btn.parent('.reply-like-item').attr('data-userLiked', replyLike);
				$btn.find('i').css('color', 'red');
				
				$btn.parent('.reply-like-item').children().eq(0).find('span').html(likeCount);
				$btn.parent('.reply-like-item').children().eq(1).find('span').html(disLikeCount);
			} else if(state === 'liked') {
				alert('게시글 공감 여부는 한번만 가능합니다. !!!');
			} else {
				alert('게시글물 공감 여부 처리가 실패했습니다. !!!');
			}
		};
		
		ajaxRequest(url, 'post', params, 'json', fn);
	});
});

// 댓글별 답글 리스트
function listReplyAnswer(parentNum) {
	if(! initReplySystem()) {
		return;
	}
		
	const url = `${postsUrl}/answer`;
	const params = 'parentNum=' + parentNum;
	
	const fn = function(data){
		addNewAnswer(data, parentNum);
	};
	
	ajaxRequest(url, 'get', params, 'json', fn);
}

function addNewAnswer(data, parentNum) {
	const listAnswers = data.listAnswers;
	const answerCount = data.answerCount;

	const htmlText = renderReplyAnswers(listAnswers);
	
	let selector;
	selector = 'div#listReplyAnswer' + parentNum;
	$(selector).html(htmlText);
	
	selector = 'span#answerCount' + parentNum;
	$(selector).html(answerCount);	
}

// 답글 버튼(댓글별 답글 등록폼 및 답글리스트)
$(function(){
	$('div#listReply').on('click', 'button.btnReplyAnswerLayout', function(){
		const $el = $(this).closest('div.reply-item-footer').next();
		
		let isHidden = $el.hasClass('d-none');
		let replyNum = $(this).attr('data-replyNum');
		
		if(isHidden) {
			// 답글 리스트 및 답글 개수
			listReplyAnswer(replyNum);
		}
		
		$el.toggleClass('d-none');
	});
});

// 댓글별 답글 등록
$(function(){
	$('div#listReply').on('click', 'button.btnSendReplyAnswer', function(){
		if(! initReplySystem()) {
			return;
		}
				
		const $el = $(this).closest('div.reply-answer');
		const replyNum = $(this).attr('data-replyNum');
		
		let content = $el.find('textarea').val().trim();
		if(! content) {
			$el.find('textarea').focus();
			return false;
		}
		
		const url = `${postsUrl}/insert`;
		const params = {num:num, content:content, parentNum:replyNum};
		
		const fn = function(data){
			$el.find('textarea').val('');
			listReplyAnswer(replyNum);
		};
		
		ajaxRequest(url, 'post', params, 'json', fn);
	});
});

// 댓글별 답글 삭제
$(function(){
	$('div#listReply').on('click', '.deleteReplyAnswer', function(){
		if(! initReplySystem()) {
			return;
		}
				
		if(! confirm('게시물을 삭제하시겠습니까 ? ')) {
		    return false;
		}
		
		let replyNum = $(this).attr('data-replyNum');
		let parentNum = $(this).attr('data-parentNum');
		
		const url = `${postsUrl}/${replyNum}`;
		const params = {mode:'answer'};
		
		const fn = function(data){
			listReplyAnswer(parentNum);
		};
		
		ajaxRequest(url, 'delete', params, 'json', fn);
	});
});

// 댓글 숨김기능
$(function(){
	$('div#listReply').on('click', '.hideReply', function(){
		if(! initReplySystem()) {
			return;
		}
				
		const $menu = $(this);
		
		let replyNum = $(this).attr('data-replyNum');
		let showReply = $(this).attr('data-showReply');
		
		let msg = '댓글을 숨김 하시겠습니까 ? ';
		if(showReply === '0') {
			msg = '댓글 숨김을 해제 하시겠습니까 ? ';
		}
		if(! confirm(msg)) {
			return false;
		}
		
		showReply = showReply === '1' ? '0' : '1';
		
		const url = `${postsUrl}/replyShowHide`;
		const params = {replyNum:replyNum, showReply:showReply};
		
		const fn = function(data){
			let $item = $($menu).closest('.reply-item-header').next('.reply-item-content');
			if(showReply === '1') {
				$item.removeClass('text-primary').removeClass('text-opacity-50');
				$menu.attr('data-showReply', '1');
				$menu.html('숨김');
			} else {
				$item.addClass('text-primary').addClass('text-opacity-50');
				$menu.attr('data-showReply', '0');
				$menu.html('표시');
			}
		};
		
		ajaxRequest(url, 'post', params, 'json', fn);
	});
});

// 답글 숨김기능
$(function(){
	$('div#listReply').on('click', '.hideReplyAnswer', function(){
		if(! initReplySystem()) {
			return;
		}
				
		const $menu = $(this);
		
		let replyNum = $(this).attr('data-replyNum');
		let showReply = $(this).attr('data-showReply');
		
		let msg = '댓글을 숨김 하시겠습니까 ? ';
		if(showReply === '0') {
			msg = '댓글 숨김을 해제 하시겠습니까 ? ';
		}
		if(! confirm(msg)) {
			return false;
		}
		
		showReply = showReply === '1' ? '0' : '1';
		
		const url = `${postsUrl}/replyShowHide`;
		const params = {replyNum:replyNum, showReply:showReply};
		
		const fn = function(data) {
			let $item = $($menu).closest('.row').next('div');
			if(showReply === '1') {
				$item.removeClass('text-primary').removeClass('text-opacity-50');
				$menu.attr('data-showReply', '1');
				$menu.html('숨김');
			} else {
				$item.addClass('text-primary').addClass('text-opacity-50');
				$menu.attr('data-showReply', '0');
				$menu.html('표시');
			}
		};

		ajaxRequest(url, 'post', params, 'json', fn);
	});
});
