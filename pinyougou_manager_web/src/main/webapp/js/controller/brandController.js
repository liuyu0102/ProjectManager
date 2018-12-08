app.controller("brandController",function ($controller,$scope,brandService) {
    //声明控制器继承代码,第一个参数:继承自哪个控制器,第二个参数:固定写法,共享$scope
    //不要忘记在html页面中导入js资源
    $controller("baseController",{$scope:$scope});

    //查询列表所有数据
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        })
    }

    /*配置发送请求的方法*/
    $scope.findPage = function (pageNum, pageSize) {
        brandService.findPage(pageNum, pageSize).success(function (response) {
            $scope.paginationConf.totalItems=response.total;
            $scope.list=response.rows;
        })
    };
    /*查询分页*/
    //定义封装查询的规格实体
    $scope.searchEntity = {};
    $scope.search = function (pageNum, pageSize) {
        brandService.search($scope.searchEntity,pageNum, pageSize).success(function (response) {
            $scope.paginationConf.totalItems=response.total;
            $scope.list=response.rows;
        })
    };
    /*保存品牌数据*/
    $scope.save = function () {
        var method=null;
        if($scope.entity.id != null){
            //修改
            method = brandService.update($scope.entity);
        }else {
            //新增
            method = brandService.add($scope.entity);
        }
        method.success(function (response) {
            if (response.success){
                //保存成功
                $scope.reloadList();
            }else {
                //保存失败
                alert(response.message)
            }
        })
    }
    /*根据id查询数据  回显,发送请求*/
    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            //直接将响应的实体赋值给绑定输入框的实体
            $scope.entity = response;
        })
    }

    /*发送删除请求*/
    $scope.dele = function () {
        if (confirm("您确定要删除吗?")){
            brandService.dele($scope.selectIds).success(function (response) {
                if (response.success){
                    //删除成功
                    $scope.reloadList();
                }else {
                    //删除失败
                    alert(response.message)
                }
            })
        }
    }
})