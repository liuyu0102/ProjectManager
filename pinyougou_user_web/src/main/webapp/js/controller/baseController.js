app.controller("baseController",function ($scope) {
    //分页配置
    $scope.paginationConf = {
        currentPage:1,  				//当前页
        totalItems:10,					//总记录数
        itemsPerPage:10,				//每页记录数
        perPageOptions:[10,20,30,40,50], //分页选项，下拉选择一页多少条记录
        onChange:function(){			//页面变更后触发的方法
            $scope.reloadList();		//启动就会调用分页组件
        }
    };
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    };
    /*获取选中集合*/
    $scope.selectIds = [];
    $scope.updateSelection = function ($event,id) {
        //判断复选框是否选中
        if ($event.target.checked){
            //选中
            $scope.selectIds.push(id);
        }else {
            //取消勾选，移除当前id值  //参数一：移除位置的元素的索引值  参数二：从该位置移除几个元素
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1)
        }
    }
    //取出json数组中的对象,将其显示在页面
    $scope.jsonStringParse = function (jsonString, key) {
        var value = "";
        var jsonArray = JSON.parse(jsonString);
        for(var i = 0; i < jsonArray.length; i++){
            if (i>0){
                value += ","+ jsonArray[i][key];
            }else {
                value += jsonArray[i][key];
            }
        }
        return value;
    }
})