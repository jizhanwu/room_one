package com.example.room_1;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import java.util.List;

//本地的一个数据仓库，对上屏蔽屏蔽了数据来源和内部实现细节，只需要拿走就行了。
//当没有网络时，可当作缓存
class WordRepository {
    //创建3个变量
    private LiveData<List<Word>> allWordLive;
    WordDatabase wordDatabase;
    private WordDao wordDao;
    //构造仓库环境上下文，使用“环境类context")库,为所有3个变量赋值
    WordRepository(Context context) {
        //通过数据库类文件，找到getDatabase（Context类型，getApplicationContext()环境方法)
        wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        //通过数据库类文件，找到getWordDao这个接口，再找到接口WordDao内的全部抽象方法名
        wordDao = wordDatabase.getWordDao();
        //通过wordDao找到WordDao,再找到里面etAllwordsLive()这个接口
        allWordLive = wordDao.xuanze_All_liebiao_paixu_neirong();
    }


    //接收ViewModel层传递过来的参数，开辟一条线程准备执行插入（UI线程插入点击监听调用了此接口）操作
    //UI层将插入点击操作参数 ->ViewModel->Repository(向仓库索取)-->仓库执行，
    void insterWords(Word... words) {
        new Inster_XianCeng(wordDao).execute(words);//新建一条插入线程(通过Dao)，带参数准备执行
    }

    void updateWords(Word... words) {
        new UpdateAsyncTask(wordDao).execute(words);
    }

    void deleteWords(Word... words) {
        new DeleteAsyncTask(wordDao).execute(words);
    }

    void deleteAllWords(Word... words) {
        new DeleteAllAsyncTask(wordDao).execute();
    }

    LiveData<List<Word>> cangkuLiebiao(){  //ViewModel层通过这个接口要仓库列表内容，给它就OK了！！
        return allWordLive;  //仓库通过 Dao这个层，worDao....();，去调取当前的数据库列表内容。
    }

   //匿名内部类来实现具体的操作逻辑

   /*AsyncTask用法，public abstract class AsyncTask<Params, Progress, Result> {
   Params代表:开始异步任务执行时传入的参数类型，即doInBackground()方法中的参数类型；
   Progress代表异步任务执行过程中，返回下载进度值的类型，即在doInBackground中调用Progress()时传入的参数类型；
   Result代表“异步任务执行完成后，返回的结果类型，即doInBackground()方法的返回值类型；
    */


    static class Inster_XianCeng extends AsyncTask<Word, Void, Void> {  //新建一个线程抽象类
        private WordDao wordDao;  //重新定义一个WordDao变量

        Inster_XianCeng(WordDao wordDao) {
            this.wordDao = wordDao;   //变量赋值
        }

        @Override                      //通过内部类方法doInBackground(在后台进行插入数据操作
        protected Void doInBackground(Word... words) {
            wordDao.insertword(words);
            return null;
        }


    }

    static class UpdateAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        UpdateAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        DeleteAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }


    }

    static class DeleteAllAsyncTask extends AsyncTask<Void, Void, Void> {
        private WordDao wordDao;

        DeleteAllAsyncTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... Voids) {
            wordDao.deleteAllWords();
            return null;
        }


    }
}
