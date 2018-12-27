 //控制层 
app.controller('cartController' ,function($scope,$controller   ,cartService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //查询购物车列表
	$scope.findCartList=function(){
        cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
                sum();
			}			
		);
	}
	//添加商品到购物车
	$scope.addItemToCartList = function (itemId,num) {
        cartService.addItemToCartList(itemId,num).success(function (response) {
			//判断是否添加成功
			if (response.success){
				//重新加载列表
                $scope.findCartList()
			}else {
				//添加失败
				alert(response.message)
			}
        })
    }
//计算总数量和总金额的方法
    sum = function () {
		//定义总数量总金额全局变量
		$scope.totalNum = 0;
        $scope.totalMoney = 0.00;
        //遍历购物车列表得到购物车对象
		for(var i = 0; i < $scope.cartList.length; i++){
            var cart = $scope.cartList[i];
            var orderItemList = cart.orderItemList;
            for(var j = 0; j < orderItemList.length; j++){
                $scope.totalNum += orderItemList[j].num;
                $scope.totalMoney += orderItemList[j].totalFee;
			}
		}
    }
});	
