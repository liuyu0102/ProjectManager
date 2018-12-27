app.controller("searchController",function ($scope,$controller,$location,searchService) {

    //控制器继承代码  参数一：继承的父控制器名称  参数二：固定写法，共享$scope对象
    $controller("baseController",{$scope:$scope});

    //定义查询条件的参数
    $scope.searchMap = {
        keywords:"",
        category:"",
        brand:"",
        price:"",
        spec:{},
        sort:"ASC",
        sortField:"",
        pageNo:1,
        pageSize:60
    };


    var keywords = $location.search()["keywords"];
    if (keywords != "undefined" ){
        //有参数
        $scope.searchMap.keywords = keywords;
    }else {
        $scope.searchMap.keywords = "手机";
    }


    //搜索查询数据
    $scope.search=function () {
        //response接收响应结果
        searchService.searchItem($scope.searchMap).success(function (response) {
            $scope.resultMap=response;
            //调用构建分页工具条代码
            buildPageLabel();
        })
    }
    //添加过滤条件
    $scope.addFilterCondition = function (key, value) {
        //判断字段是字符串还是对象
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = value;
        }else {
            $scope.searchMap.spec[key] = value;
        }
        //调用搜索方法
        $scope.search();
    }
    //移除查询条件
    $scope.removeSearchItem = function (key) {
        //判断字段是字符串还是对象
        if (key == 'category' || key == 'brand' || key == 'price'){
            $scope.searchMap[key] = "";
        }else {
            //使用delete删除整个条件框
            delete $scope.searchMap.spec[key];
        }
        //调用搜索方法
        $scope.search();
    }
    //排序查询组装数据
    $scope.sortSearch = function (sortField,sort) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sort = sort;
        //调用搜索方法
        $scope.search();
    }

    //构建分页工具条代码
    buildPageLabel=function(){
        $scope.pageLabel = [];// 新增分页栏属性
        var maxPageNo = $scope.resultMap.totalPages;// 得到最后页码

        // 定义属性,显示省略号
        $scope.firstDot = true;
        $scope.lastDot = true;

        var firstPage = 1;// 开始页码
        var lastPage = maxPageNo;// 截止页码

        if ($scope.resultMap.totalPages > 5) { // 如果总页数大于5页,显示部分页码
            if ($scope.resultMap.pageNo <= 3) {// 如果当前页小于等于3
                lastPage = 5; // 前5页
                // 前面没有省略号
                $scope.firstDot = false;

            } else if ($scope.searchMap.pageNo >= lastPage - 2) {// 如果当前页大于等于最大页码-2
                firstPage = maxPageNo - 4; // 后5页
                // 后面没有省略号
                $scope.lastDot = false;
            } else {// 显示当前页为中心的5页
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            // 页码数小于5页  前后都没有省略号
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        // 循环产生页码标签
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }


    //分页查询
    $scope.queryForPage=function(pageNo){
        $scope.searchMap.pageNo=pageNo;

        //执行查询操作
        $scope.search();

    }

    //分页页码显示逻辑分析：
    // 1,如果页面数不足5页,展示所有页号
    // 2,如果页码数大于5页
    // 1) 如果展示最前面的5页,后面必须有省略号.....
    // 2) 如果展示是后5页,前面必须有省略号
    // 3) 如果展示是中间5页,前后都有省略号

    // 定义函数,判断是否是第一页
    $scope.isTopPage = function() {
        if ($scope.searchMap.pageNo == 1) {
            return true;
        } else {
            return false;
        }
    }
    // 定义函数,判断是否最后一页
    $scope.isLastPage = function() {
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return true;
        } else {
            return false;
        }
    }
});