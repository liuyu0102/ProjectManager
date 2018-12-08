 //控制层 
app.controller('typeTemplateController' ,function($scope,$controller,typeTemplateService,brandService,specificationService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//品牌列表
                $scope.entity.brandIds = JSON.parse(response.brandIds)
                //规格列表
                $scope.entity.specIds = JSON.parse(response.specIds)
                //扩展列表
                $scope.entity.customAttributeItems = JSON.parse(response.customAttributeItems)
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=typeTemplateService.update($scope.entity); //修改
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框
        if (confirm("您确定要删除吗?")) {
            typeTemplateService.dele($scope.selectIds).success(
                function (response) {
                    if (response.success) {
                        $scope.reloadList();//刷新列表
                    }
                }
            );
        }
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

    /**
	 * 映射下拉选项的值
     * @type {{data: *[]}}
     */
    $scope.brandList = {
        data: []
    };
    /**
	 * 调用service方法发情请求,接收响应数据,并设置给关联品牌的下拉选项框
     */
    $scope.selectBrandList = function () {
		brandService.selectBrandList().success(function (response) {
            $scope.brandList.data = response;
        })
    }
    /**
     * 映射下拉选项的值
     * @type {{data: *[]}}
     */
    $scope.specList = {
        data: []
    };
    $scope.selectSpecList = function () {
        specificationService.selectSpecList().success(function (response) {
            $scope.specList.data = response;
        })
    }
    //新增扩展属性
    $scope.entity={customAttributeItems:[]};
    $scope.addRow = function () {
        $scope.entity.customAttributeItems.push({});
    }
    //删除扩展属性
    $scope.deleRow = function (index) {
        $scope.entity.customAttributeItems.splice(index, 1);
    }
});	
