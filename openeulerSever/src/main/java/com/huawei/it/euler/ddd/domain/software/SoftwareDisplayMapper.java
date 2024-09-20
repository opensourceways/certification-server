package com.huawei.it.euler.ddd.domain.software;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huawei.it.euler.ddd.domain.software.SoftwareDisplayPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface SoftwareDisplayMapper  extends BaseMapper<SoftwareDisplayPO> {

//    Integer insert(SoftwareDisplay softwareDisplay);
//
//    void batchDelete(List<Integer> idList);
//
//    IPage<SoftwareDisplay> getHiddenPage(@Param("uuid") String uuid,  @Param("productName") String productName, IPage<SoftwareDisplay> page);

}
