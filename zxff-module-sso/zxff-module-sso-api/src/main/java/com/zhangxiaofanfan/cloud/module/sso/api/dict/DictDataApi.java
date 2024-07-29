package com.zhangxiaofanfan.cloud.module.sso.api.dict;

import com.zhangxiaofanfan.cloud.module.common.constant.http.SsoApiConstants;
import com.zhangxiaofanfan.cloud.module.common.pojo.CommonResult;
import com.zhangxiaofanfan.cloud.module.sso.api.dict.dto.response.DictDataRespDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

import static com.zhangxiaofanfan.cloud.module.common.util.collection.CollectionUtils.convertList;

@FeignClient(name = SsoApiConstants.NAME) // TODO 芋艿：fallbackFactory =
@Tag(name = "RPC 服务 - 字典数据")
public interface DictDataApi {

    String PREFIX = SsoApiConstants.PREFIX + "/dict-data";

    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验字典数据们是否有效")
    @Parameters({
            @Parameter(name = "dictType", description = "字典类型", example = "SEX", required = true),
            @Parameter(name = "descriptions", description = "字典数据值的数组", example = "1,2", required = true)
    })
    CommonResult<Boolean> validateDictDataList(@RequestParam("dictType") String dictType,
                                               @RequestParam("values") Collection<String> values);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获得指定的字典数据")
    @Parameters({
            @Parameter(name = "dictType", description = "字典类型", example = "SEX", required = true),
            @Parameter(name = "description", description = "字典数据值", example = "1", required = true)
    })
    CommonResult<DictDataRespDTO> getDictData(@RequestParam("dictType") String dictType,
                                              @RequestParam("value") String value);

    @GetMapping(PREFIX + "/parse")
    @Operation(summary = "解析获得指定的字典数据")
    @Parameters({
            @Parameter(name = "dictType", description = "字典类型", example = "SEX", required = true),
            @Parameter(name = "label", description = "字典标签", example = "男", required = true)
    })
    CommonResult<DictDataRespDTO> parseDictData(@RequestParam("dictType") String dictType,
                                                @RequestParam("label") String label);

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获得指定字典类型的字典数据列表")
    @Parameter(name = "dictType", description = "字典类型", example = "SEX", required = true)
    CommonResult<List<DictDataRespDTO>> getDictDataList(@RequestParam("dictType") String dictType);

    /**
     * 获得字典数据标签列表
     *
     * @param dictType 字典类型
     * @return 字典数据标签列表
     */
    default List<String> getDictDataLabelList(String dictType) {
        List<DictDataRespDTO> list = getDictDataList(dictType).getData();
        return convertList(list, DictDataRespDTO::getLabel);
    }

}
