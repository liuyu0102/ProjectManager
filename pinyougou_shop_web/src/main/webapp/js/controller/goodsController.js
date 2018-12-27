//控制层
app.controller('goodsController', function ($scope, $controller, goodsService, itemCatService, typeTemplateService,uploadService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }


    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function (id) {
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            //使用富文本编辑器获取商品介绍的信息
            $scope.entity.goodsDesc.introduction = editor.html();
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.entity = {};
                    editor.html("");
                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }
    //上传图片
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(function (response) {
            //判断是否上传成功
            if (response.success){
                //成功
                $scope.imageEntity.url = response.message;
            }else {
                //失败
                alert(response.message)
            }
        })
    }
    //定义组合数据结构
    $scope.entity={goods:{isEnableSpec:'1'},goodsDesc:{itemImages:[],specificationItems:[]},itemList:[]}
    //增加
    $scope.addImageEntity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.imageEntity)
    }
    //删除
    $scope.deleImageEntity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1)
    }


    //查询一级分类
    $scope.selectItemCat1List = function () {
        itemCatService.findByParentId(0).success(function (response) {
            $scope.itemCat1List = response;
        })
    }
    //基于一级分类,联动查询二级分类 参数一:一级分类绑定的变量值ng-model="值",参数二:监控之后要做的事
    //newValue 变化后的值    oldValue  变化前的值
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat2List = response;
            $scope.itemCat3List = {};
        })
    })
    //基于二级分类查询三级分类
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
        })
    })
    //基于三级分类查询模板id
    $scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        });
    })
    //基于模板id查询品牌列表数据
    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        typeTemplateService.findOne(newValue).success(function (response) {
            //将得到的brandIds的json字符串转为json数据
            $scope.brandList = JSON.parse(response.brandIds);

            //将扩展属性的json字符串转为json数组赋值给goodsdesc中的扩展属性
            //[{"text":"内存大小"},{"text":"颜色"}]
            $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
        });

        //基于模板的变化id  查询规格及规格选项的列表
        typeTemplateService.findSpecList(newValue).success(function (response) {
            $scope.specList = response;
        })
    })
    $scope.updateSpecAttribute = function ($event, specName, specOptionName) {
        //调用基础通用函数,用于判断是否存在规格对象
        ////[{"attributeName":"网络","attributeValue":["移动3G","移动4G"]},{}]
        var specObject = $scope.getObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",specName);
        //判断是否存在规格对象
        if (specObject != null ){
            //存在
            if ($event.target.checked){
                //勾选
                specObject.attributeValue.push(specOptionName);
            }else {
                //取消勾选
                var index = specObject.attributeValue.indexOf(specOptionName)
                specObject.attributeValue.splice(index,1)
                //如果全部取消
                if (specObject.attributeValue.length <= 0){
                    var indexAll =$scope.entity.goodsDesc.specificationItems.indexOf(specObject)
                    $scope.entity.goodsDesc.specificationItems.splice(indexAll,1)
                }
            }

        }else {
            //不存在
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":specName,"attributeValue":[specOptionName]})
        }
    }
    $scope.createItemList = function () {
        // 初始化一个sku商品列表,itemList : [{spec:{},price:0,num:99999,status:"1",isDefault:"0"},{}],
        $scope.entity.itemList = [{spec:{},price:0,num:99999,status:"1",isDefault:"0"}]
        // 使用规格结果集列表: specList:[{"attributeName":"网络","attributeValue":["移动3G"]} {}]
        var specList = $scope.entity.goodsDesc.specificationItems;
        //如果选项为空,则清空sku商品列表
        if (specList == 0){
            $scope.entity.itemList = [];
        }
        // 先遍历specList  得到规格名称,规格选项数组,创建赋值方法  传递参数为itemList 列表,规格名称,规格选项数组 用sku列表接收
        for(var i = 0; i < specList.length; i++){
            $scope.entity.itemList=addColumn($scope.entity.itemList,specList[i].attributeName,specList[i].attributeValue)
        }
    }
    addColumn = function (itemList, specName, specOptions) {
    //构建赋值方法,创建一个新数组newList用于返回赋值后的列表,方法中遍历itemList列表,得到spec对象,将对象使用深克隆
    var newList = [];
    //spec:{"机身内存":"16G","网络":"联通3G"}
    for(var i = 0; i < itemList.length; i++){
       var item = itemList[i]
        //item : {spec:{},price:0,num:99999,status:"1",isDefault:"0"}
        // 遍历规格选项数组,得到每一个规格,由于规格名称为变量使用  对象[key] 的方式赋值
        for(var j = 0; j < specOptions.length; j++){
            var newItem = JSON.parse(JSON.stringify(item));
            newItem.spec[specName] = specOptions[j];
            newList.push(newItem)
        }
    }
    // 将克隆的item添加到新数组中,返回
        return newList;
    }
    $scope.status = ['未审核','已审核','审核未通过','关闭'];
    //定义查询所有分类的方法
    //声明数组
    $scope.itemCatList = [];
    $scope.selectItemCatList = function () {
        itemCatService.findAll().success(function (response) {
            //遍历得到的分类列表
            for(var i = 0; i < response.length; i++){
                //定义数组,数组结构为  itemCatList[分类id] = 分类名
                $scope.itemCatList[response[i].id] = response[i].name
            }
        })
    }

    $scope.isMarketable = ['已下架','已上架'];
    //批量上下架
    $scope.updateIsMarketable=function(isMarketable){
        //获取选中的复选框
        goodsService.updateIsMarketable( $scope.selectIds ,isMarketable).success(
            function(response){
                if(response.success){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }else {
                    alert(response.message)
                }
            }
        );
    }


















});