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

    //门口页面与搜索页面对接,路由传参,?前加#
    $scope.search = function () {
        location.href = "http://search.pinyougou.com/search.html#?keywords="+$scope.keywords;
    }
});