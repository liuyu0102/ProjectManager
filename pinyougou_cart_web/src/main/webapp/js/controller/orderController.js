 //控制层 
app.controller('orderController' ,function($scope,$controller   ,cartService,addressService,orderService){
	
	$controller('baseController',{$scope:$scope});//继承

	//查询用户地址列表
	$scope.findAddressListByUserId = function () {
        addressService.findAddressListByUserId().success(function (response) {
            $scope.addressList=response;
            for(var i = 0; i < $scope.addressList.length; i++){
                //判断地址中有无默认地址
                if ($scope.addressList[i].isDefault == '1'){
                    $scope.address = $scope.addressList[i]
                    break;
                }
            }
            if ($scope.address == null){
                $scope.address = $scope.addressList[0];
            }

        })
    }
    //定义全局地址对象
    $scope.address = null;
	//定义判断地址是否为默认地址
    $scope.isSelected = function (addr) {
        if (addr == $scope.address){
            return true;
        }else {
            return false;
        }
    }
    //切换选中
    $scope.updateSelected = function (addr) {
        $scope.address = addr;
    }
    $scope.entity = {paymentType:'1'}

    $scope.updatePaymentType = function (type) {
        $scope.entity = type;
    }
	
    //查询购物车列表
	$scope.findCartList=function(){
        cartService.findCartList().success(
			function(response){
				$scope.cartList=response;
                sum();
			}			
		);
	}
	//保存订单
    $scope.save = function () {
        $scope.entity.receiver=$scope.address.contact;//联系人
        $scope.entity.receiverAreaName=$scope.address.address;//联系人地址
        $scope.entity.receiverMobile=$scope.address.mobile;//联系电话
        orderService.add($scope.entity).success(function (response) {
            if (response.success){
                location.href = "pay.html";
            }else {
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
