app.controller("indexController",function ($scope,$controller,contentService) {

    //控制器继承代码  参数一：继承的父控制器名称  参数二：固定写法，共享$scope对象
    $controller("baseController",{$scope:$scope});

    //获取用户名
    $scope.findByCategoryId=function (categoryId) {
        //response接收响应结果
        contentService.findByCategoryId(categoryId).success(function (response) {
            $scope.contentList=response;
        })
    }
});