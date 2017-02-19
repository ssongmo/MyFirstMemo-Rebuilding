# MyFirstMemo 앱 rebuilding
- 계속해서 앱의 기능을 추가하던중 모든 프래그먼트와 액티비티들이 지속해서 DBHelper를 접근하는 것을 알 수 있었다.
-----
**데이터의 흐름**
``` @mermaid
graph LR
MainActivity --> ListFragment
ListFragment --> ListAdapter
ListFragment --> DBHelper
DBHelper --> ListFragment
ListAdapter --> ListAdapter.ViewHolder
MainActivity --> EditFragment
MainActivity --> DBHelper
DBHelper --> MainActivity
EditFragment --> DBHelper
DBHelper-->EditFragment
DBLogFragment --> MainActivity
MainActivity --> DBLogFragment
EditFragment --> DBLogFragment
```
이런 상황이라면 카메라를 추가하거나 새로운 액티비티, 프래그먼트를 추가할 경우 더 힘들어질 것이라고 판단하여 리빌딩을 해봤다. 그전에 내가 만든 클래스와 그 안에 주요 매소드들이 어떤 것이 있고 어떻게 접근을 해야할지 정리해보았다.
## Memo의 구조

#### MainActivity - activity_main.xml
- TabLayout과 ViewPager로 구성.

#### EditFragment - fragment_edit.xml
- Memo를 작성 하는 공간.
- 다이얼로그 메세지 버튼이 포함 되어있으며 이를 통해 앨범과 카메라를 구현.
- Save Button : 메모 입력 후 버튼 클릭시 내용 저장 후 뷰 페이지 2번째로 이동.

#### ListFragment - layout_list.xml
- 저장된 메모를 RecyclerView를 통해 보여주는 공간.
- RecyclerView안에는 CardView가 들어가 있다.

#### ListAdapter - layout_list_item.xml, MemoPageActivity.xml
- Adapter로써 Holder를 생성하고, 그 안에 cardView와 DeleteButton을 포함하고 있다.
- cardView는 layout_list_item 레이아웃 안에 EditFragment의 데이터를 가지고 있어야 한다.
- cardView를 클릭하면 MemoPageActivity(메모 내용이 들어있는 액티비티)로 이동해야 한다. (아직 미구현 2/18)

#### Memo - X
- 메모에 필요한 요소들의 집합. id, memo, date로 구성.
```
  id: 카드뷰를 클릭했을 때 클릭한 카드의 뷰(Memo)로 이동하기 위해 아이디를 부여.
  클릭한 카드에 해당하는 아이디를 통해 가져온다.
  memo: memo한 데이터.
  date: 작성한 날짜와 시간.
  ```

- DBHelper와 이어져있다.

#### DBHelper
- 싱글톤으로 구성.
- DBHelper는 SqlLite query로 Memo데이터를 설정하고, 각 activity, fragment를 database와 연결해주는 다리역할을 한다.   

각 매소드의 역할에 대해서 생각해보고, 어떤매소드로 각각의 activity와 fragment가 이어져있는지 파악하자.

## 각 클래스의 Method
**MainActivity Method.**
```
1. onCreate() : TabLayout, ViewPager, 사용되는 fragment를 정의하고 리스너를 설정.
2. loadData() : DBhelper를 통해 Memo 자료형을 가진 데이터를 불러온다.
3. saveToList(Memo memo) : DBhelper를 이용해서 메모를 저장하고, RecyclerView가 있는 ListFragment로 보낸다.  //저장만 해야하는것 아닌가? ListFragment로 보내는 매소드를 만드는 것이 효율적일까?
4. class PagerAdapter extends FragmentStatePagerAdapter{} : PagerAdapter를 통해 프래그먼트와 activity를 연결해준다.
```
**EditFragment**
```
인터페이스가 존재한다.
1. Fragment를 구성하는 기본적인 매소드가 있다.

- onCreate() : Fragment가 처음 생성될 때 시작되는 매소드.
- onCreateView() : Fragment의 View를 띄워줄 때 시작되는 매소드. onCreate와 거의 동시에 시작된다.
                 이 안에서 필요한 뷰(?)들을 선언 해준다. (준무가 말했던 용어가 뭐였지..버튼이라던가 텍스트뷰같은걸 다 뭐라고 한다고했는데 그것이 뷰였나....)
                 inflater를 통해 보여질 layout을 설정한다.
- onAttach() : 잘 모르겠다.
- onDetach() : 잘 모르겠다.

2. onClick() : onCreateView에서 사용되는 버튼의 기능을 구현.
```

**ListFragment**
1. onCreate() : 매소드가 실행될 때, DB쿼리문을 통해 DB에 저장된 메모내용을 불러온다.
2. onCreateView() : inflater를 통해 layout_list(메모카드 뭉치가 저장되는 페이지)를 뿌려준다.
                    RecyclerView 선언, 리스너 정의.
                    안에 사용되는 카드뷰와 연결 해주기 위해서 어댑터 세팅.
```
                    if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        } 이부분 자동생성되서 잘 모르겠음..
```
3. onAttach, onDetach

**ListAdapter**
```
1. 메모가 저장되는 카드뷰를 다른 Activity, Fragment에서 사용할 수 있도록 연결해주는 어댑터.
2. 홀더는 카드에 들어가는 데이터를 동째로 하나의 홀더로 잡아주는 역할을 수행한다.
3. onCreateViewHolder() : Holder를 뿌려줄 layout_list_item을 inflater.
4. **public class ViewHolder extends RecyclerView.ViewHolder{}**  : ViewHolder Class엔 2번에서 말한 카드에 들어가는 버튼, 텍스트등 뷰를 선언하고 사용하는 공간이다. onClick() 리스너 함수를 구현해서 카드뷰와 삭제(이미지버튼)을 구현.
```

**Memo**
```
1. @DatabaseTable(tableName = "memo")
    public class Memo { ... }           
    : Table 생성.


2. @DatabaseField(generatedId = true)
    int id;                           
    : field명 설정(연속적으로 생성)    

3.  //create 시에 사용할 생성자.
    public Memo(String memo, Date date){
        this.memo = memo;
        this.date = date;
    }

4. getter, setter 함수 설정.    
```

**DBhelper**
```
1. onCreate() :  처음 DB Table이 생성될때만 호출된다.
2. onUpgrade() : 한번 생성되면, 이후엔 onUpgrade함수만 호출된다. 때문에 이 안에 onCreate함수가 있다.
```
---

*클래스와 매소드들을 한번씩 다시 둘러보며 새로운 데이터흐름을 빌딩해봤다.*

#### **New 데이터의 흐름**
``` @mermaid
graph LR
MainActivity --> ListFragment
ListFragment --> ListAdapter
ListAdapter --> ListAdapter.ViewHolder
MainActivity --> EditFragment
MainActivity --> DBHelper
DBHelper --> MainActivity

```
MainActivity에 DBHelper의 리스너를 구현해서 오로지 MainActivity를 통해서 Memo의 데이터에 접근할 수 있도록 설정 하였다. 다음으로 메모를 카드뷰에 저장하고 카드뷰를 클릭했을때 작성했던 내용을 로드할 액티비티가 필요했다.그래서 새로운 Activity Class를 생성하였다. 위에서 잠깐 언급했던 MemoPageActivity이다. 그러나 문제가 생겼다. MemoPageActivity에서 MainActivity클래스의 메모를 접근할 수 없었다.
그 결과 아래의 데이터 흐름이 완성되었다.

**신신 데이터의 흐름**
``` @mermaid
graph LR
MemoPageActivity
MainActivity --> DBLogFragment
MainActivity --> ListFragment
ListFragment --> ListAdapter
ListAdapter --> ListAdapter.ViewHolder
MainActivity --> EditFragment
MainActivity --> DBHelper
DBHelper --> MainActivity
MemoPageActivity  --> DBHelper
DBHelper --> MemoPageActivity

```
