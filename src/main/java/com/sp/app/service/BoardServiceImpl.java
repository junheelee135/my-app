package com.sp.app.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sp.app.common.MyUtil;
import com.sp.app.common.StorageService;
import com.sp.app.domain.entity.Board;
import com.sp.app.mapper.BoardMapper;
import com.sp.app.repository.BoardRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
	private final BoardRepository boardRepository;
	private final BoardMapper mapper;
	
	private final StorageService storageService;
	private final MyUtil myUtil;
	
	@Transactional
	@Override
	public void insertBoard(Board dto, String uploadPath) throws Exception {
		try {
			if(! dto.getSelectFile().isEmpty()) {
				String saveFilename = storageService.uploadFileToServer(dto.getSelectFile(), uploadPath);
				dto.setSaveFilename(saveFilename);
				dto.setOriginalFilename(dto.getSelectFile().getOriginalFilename());
			}

			boardRepository.save(dto);
		} catch (Exception e) {
			log.info("insertBoard : ", e);
			
			throw e;
		}
	}

	@Transactional
	@Override
	public void updateBoard(Board dto, String uploadPath) throws Exception {
		try {
			if(dto.getSelectFile() != null && ! dto.getSelectFile().isEmpty()) {
				if(! dto.getSaveFilename().isBlank()) {
					deleteUploadFile(uploadPath, dto.getSaveFilename());
				}
				
				String saveFilename = storageService.uploadFileToServer(dto.getSelectFile(), uploadPath);
				dto.setSaveFilename(saveFilename);
				dto.setOriginalFilename(dto.getSelectFile().getOriginalFilename());
			}			

			boardRepository.save(dto);
		} catch (Exception e) {
			log.info("updateBoard : ", e);
			
			throw e;
		}
	}
	
	@Transactional
	@Override
	public void deleteBoard(long num, String uploadPath, Long member_id, int userLevel) throws Exception {
		try {
			Board dto = Objects.requireNonNull(findById(num));
			if (userLevel < 51 && dto.getMember_id() != member_id) {
				return;
			}

			deleteUploadFile(uploadPath, dto.getSaveFilename());

			boardRepository.deleteById(num);

		} catch (NullPointerException e) {
		} catch (Exception e) {
			log.info("deleteBoard : ", e);
			
			throw e;
		}
	}
	
	@Transactional
	@Override
	public void updateHitCount(long num) throws Exception {
		// 조회수 증가
		try {
			Board dto = boardRepository.findById(num)
					.orElseThrow(() -> new NullPointerException("게시물이 존재하지 않습니다."));
			
			dto.setHitCount(dto.getHitCount() + 1);
			
			boardRepository.save(dto);
		} catch (NullPointerException e) {
		} catch (Exception e) {
			log.info("updateHitCount : ", e);
			
			throw e;
		}
	}
	
	@Override
	public List<Board> listBoard(Map<String, Object> map) {
		List<Board> list = null;

		try {
			list = mapper.listBoard(map);

			for(Board dto : list) {
				dto.setName(myUtil.nameMasking(dto.getName()));
			}
		} catch (Exception e) {
			log.info("listBoard : ", e);
		}

		return list;
	}

	@Override
	public int dataCount(Map<String, Object> map) {
		int result = 0;

		try {
			result = mapper.dataCount(map);
		} catch (Exception e) {
			log.info("dataCount : ", e);
		}

		return result;
	}

	@Override
	public Board findById(long num) {
		Board dto = null;

		try {
			dto = mapper.findById(num);
		} catch (Exception e) {
			log.info("findById : ", e);
		}

		return dto;
	}

	@Override
	public Board findByPrev(Map<String, Object> map) {
		Board dto = null;

		try {
			dto = mapper.findByPrev(map);
		} catch (Exception e) {
			log.info("findByPrev : ", e);
		}

		return dto;
	}

	@Override
	public Board findByNext(Map<String, Object> map) {
		Board dto = null;

		try {
			dto = mapper.findByNext(map);
		} catch (Exception e) {
			log.info("findByNext : ", e);
		}

		return dto;
	}

	@Override
	public void insertBoardLike(Map<String, Object> map) throws Exception {
		try {
			mapper.insertBoardLike(map);
		} catch (Exception e) {
			log.info("insertBoardLike : ", e);
			throw e;
		}
	}

	@Override
	public void deleteBoardLike(Map<String, Object> map) throws Exception {
		try {
			mapper.deleteBoardLike(map);
		} catch (Exception e) {
			log.info("deleteBoardLike : ", e);
			throw e;
		}
	}

	@Override
	public int boardLikeCount(long num) {
		int result = 0;
		
		try {
			result = mapper.boardLikeCount(num);
		} catch (Exception e) {
			log.info("boardLikeCount : ", e);
		}
		
		return result;
	}

	@Override
	public boolean isUserBoardLiked(Map<String, Object> map) {
		boolean result = false;
		try {
			Board dto = mapper.hasUserBoardLiked(map);
			if(dto != null) {
				result = true; 
			}
			// result = Objects.nonNull( mapper.hasUserBoardLiked(map) );
		} catch (Exception e) {
			log.info("isUserBoardLiked : ", e);
		}
		
		return result;
	}
	
	@Override
	public boolean deleteUploadFile(String uploadPath, String filename) {
		return storageService.deleteFile(uploadPath, filename);
	}
}
