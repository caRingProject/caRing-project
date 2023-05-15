package com.example.caRing.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.caRing.model.board.Board;
import com.example.caRing.model.board.Location;
import com.example.caRing.model.board.LocationPrice;
import com.example.caRing.model.board.car.AttachedFile;
import com.example.caRing.model.board.car.Brand;
import com.example.caRing.model.board.car.Car;
import com.example.caRing.model.board.car.CarType;
import com.example.caRing.model.board.car.Feature;
import com.example.caRing.model.board.car.Fuel;
import com.example.caRing.model.board.car.OptionList;

@Mapper
public interface BoardMapper {
	
	// 차 등록
	List<Brand> findBrand();
	List<CarType> findCarType();
	CarType findCarTypeById(Long carType_id);
	List<Fuel> findFuel();
	Fuel findFuelById(Long fuel_id);
	List<Feature> findFeature();
	void updateCar(Car car);
	
	void saveCar(Car car);
	void saveOption(OptionList optionList);
	void saveFile(AttachedFile file);
	
	String findOptionListById(Long carInfo_id);
	
	// 게시글 등록
    void saveBoard(Board board);
    String setTitle(Long carInfo_id);	
    
    // 보드 전체 출력
    List<Board> findAllBoards();
    
    // 호스트 이메일로 게시글 리스트 출력
    List<Board> findBoardsByEmail(String host_email);
    
    // 호스트 이메일로 차 리스트 출력
    List<Car> findCarInfoByEmail(String host_email);
    Car findCarInfoByCarInfoId(Long carInfo_id);
    
    // 보드 아이디로 게시글 출력
    Board findBoard(Long board_id);
    
    // 사진 출력
    List<AttachedFile> findFileByAttachedFileId(Long carInfo_id);
    
    List<Board> findLocation(Location location);
    List<Board> findLocationAsc(Location location);
    List<Board> findLocationDesc(Location location);
    List<Board> findLocationdistance(Location location);
    List<Board> findLocationdPrice(LocationPrice locationPrice);
    
}
