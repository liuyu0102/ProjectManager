//服务层
app.service('uploadService',function($http){

	//定义方法,由于向后端传递参数为file类型,则发送请求方式为(可类比于ajax异步请求方式)
	this.uploadFile = function () {
        //基于anguarJs与H5的代替表单数据的对象完成上传图片功能
        var formData = new FormData();
        //参数一:后端控制器方法的参数名
        //参数二:页面input标签的文件选择域file的files数组的第一个值
        formData.append("file",file.files[0]);
		return $http({
			method:"post",
			url:"../upload/uploadFile.do",
			data:formData,
            headers: {'Content-Type':undefined},//enctype="multipart/form-data"
            transformRequest: angular.identity //基于angularJs
		});
    }
});
