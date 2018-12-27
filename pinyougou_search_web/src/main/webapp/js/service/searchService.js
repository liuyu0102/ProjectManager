//服务层
app.service('searchService',function($http){

    //根据分类id查询广告列表
    this.searchItem=function(searchMap){
        return $http.post('search/searchItem.do',searchMap);
    }
});
