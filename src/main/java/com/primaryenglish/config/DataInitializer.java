package com.primaryenglish.config;

import com.primaryenglish.entity.*;
import com.primaryenglish.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private CategoryRepository categoryRepo;
    @Autowired private VocabularyRepository vocabRepo;
    @Autowired private ArticleRepository articleRepo;

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        // 若資料庫為空，自動執行 data.sql 初始化分類與單字資料
        if (categoryRepo.count() == 0) {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("data.sql"));
            populator.execute(dataSource);
        }
        initArticles();
    }

    private void initArticles() {
        if (articleRepo.count() > 0) return;

        // Article 1: My Pet Dog (Grade 3, Easy)
        Article a1 = new Article(
            "My Pet Dog",
            "我的寵物狗",
            "I have a pet dog. His name is Buddy. Buddy is brown and white. He has a long tail and big ears. Every morning, Buddy runs in the park with me. He likes to play with a red ball. Buddy is very friendly. He likes to meet new friends. When I come home from school, Buddy wags his tail and jumps up to greet me. I love my pet dog very much.",
            "我有一隻寵物狗。他的名字是巴弟。巴弟是棕色和白色的。他有一條長長的尾巴和大大的耳朵。每天早上，巴弟和我一起到公園跑步。他喜歡玩一顆紅色的球。巴弟非常友善。他喜歡認識新朋友。當我放學回家時，巴弟會搖尾巴並跳起來迎接我。我非常愛我的寵物狗。",
            "easy", "3", "Animals", 89
        );
        a1.addQuestion(new ReadingQuestion("What is the dog's name?", "Bobby", "Buddy", "Billy", "Benny", "B", 1));
        a1.addQuestion(new ReadingQuestion("What color is Buddy?", "Black and white", "Brown and white", "Yellow and brown", "White and gray", "B", 2));
        a1.addQuestion(new ReadingQuestion("What does Buddy like to play with?", "A stick", "A frisbee", "A red ball", "A toy car", "C", 3));
        a1.addQuestion(new ReadingQuestion("Where does Buddy run every morning?", "In the garden", "In the park", "On the street", "At the beach", "B", 4));
        a1.addQuestion(new ReadingQuestion("How does Buddy greet the writer after school?", "He barks loudly", "He wags his tail and jumps up", "He sleeps", "He runs away", "B", 5));
        articleRepo.save(a1);

        // Article 2: My Favorite Food (Grade 3, Easy)
        Article a2 = new Article(
            "My Favorite Food",
            "我最愛的食物",
            "My favorite food is pizza. Pizza is round and flat. It has cheese, tomato sauce, and many toppings on top. My favorite toppings are pepperoni and mushrooms. I also like pineapple on my pizza. My mother makes pizza at home on weekends. She puts the pizza in the oven. After fifteen minutes, the pizza is hot and delicious. I usually eat three pieces of pizza. Pizza makes me happy!",
            "我最愛的食物是披薩。披薩是圓圓扁扁的。上面有起司、番茄醬和許多配料。我最愛的配料是義式臘腸和蘑菇。我也喜歡在披薩上加鳳梨。我媽媽週末會在家做披薩。她把披薩放進烤箱。十五分鐘後，披薩熱騰騰又好吃。我通常會吃三片披薩。披薩讓我好開心！",
            "easy", "3", "Food", 92
        );
        a2.addQuestion(new ReadingQuestion("What is the writer's favorite food?", "Hamburger", "Pizza", "Noodles", "Rice", "B", 1));
        a2.addQuestion(new ReadingQuestion("What shape is pizza?", "Square", "Triangle", "Round and flat", "Long", "C", 2));
        a2.addQuestion(new ReadingQuestion("What toppings does the writer like best?", "Onions and peppers", "Pepperoni and mushrooms", "Ham and corn", "Beef and cheese", "B", 3));
        a2.addQuestion(new ReadingQuestion("Who makes pizza at home?", "The writer's father", "The writer's mother", "The writer", "The writer's sister", "B", 4));
        a2.addQuestion(new ReadingQuestion("How long does the pizza cook in the oven?", "Five minutes", "Ten minutes", "Fifteen minutes", "Twenty minutes", "C", 5));
        articleRepo.save(a2);

        // Article 3: A Day at School (Grade 4, Medium)
        Article a3 = new Article(
            "A Day at School",
            "上學的一天",
            "My name is Tom. I am a student at Sunny Elementary School. Every day, I get up at seven o'clock. I eat breakfast and then walk to school with my friend Jack. Our school starts at eight thirty. We have six classes every day. My favorite class is English. Our English teacher, Miss Lin, is very kind. She always tells us interesting stories. At twelve o'clock, we have lunch in the cafeteria. I usually eat rice, vegetables, and chicken. In the afternoon, we have art class and music class. I like to draw pictures in art class. After school, I play basketball with my classmates for one hour. Then I go home and do my homework. I go to bed at nine o'clock. This is my busy but happy day at school.",
            "我的名字是湯姆。我是陽光國小的學生。每天早上我七點起床。我吃早餐，然後和朋友傑克一起走路去上學。我們學校八點半開始上課。我們每天有六節課。我最喜歡的課是英文課。我們的英文老師林老師非常親切。她總是給我們講有趣的故事。十二點時，我們在食堂吃午餐。我通常吃米飯、蔬菜和雞肉。下午我們有美術課和音樂課。我喜歡在美術課畫畫。放學後，我和同學打一小時籃球。然後我回家做作業。我九點上床睡覺。這是我在學校忙碌但快樂的一天。",
            "medium", "4", "School", 145
        );
        a3.addQuestion(new ReadingQuestion("What time does Tom get up?", "Six o'clock", "Seven o'clock", "Eight o'clock", "Nine o'clock", "B", 1));
        a3.addQuestion(new ReadingQuestion("Who does Tom walk to school with?", "His brother", "His sister", "His friend Jack", "His mother", "C", 2));
        a3.addQuestion(new ReadingQuestion("What is Tom's favorite class?", "Math", "Science", "English", "Chinese", "C", 3));
        a3.addQuestion(new ReadingQuestion("What does Tom eat for lunch?", "Noodles and beef", "Rice, vegetables, and chicken", "Bread and milk", "Pizza and salad", "B", 4));
        a3.addQuestion(new ReadingQuestion("What does Tom do after school?", "He watches TV", "He plays basketball for one hour", "He goes to the library", "He takes a nap", "B", 5));
        articleRepo.save(a3);

        // Article 4: The Weather (Grade 4, Medium)
        Article a4 = new Article(
            "The Weather",
            "天氣",
            "Weather is important in our daily life. In spring, the weather is warm and rainy. Flowers bloom and birds sing. Many people like to go on picnics in spring. In summer, it is very hot. The sun shines brightly. Children love to eat ice cream and swim in the pool. In autumn, the weather becomes cool. Leaves turn yellow, orange, and red. Farmers harvest fruits and vegetables. In winter, it is cold. Sometimes it snows in the mountains. People wear warm coats and scarves. We can see beautiful snowmen in the park. Every season is special. I like spring best because everything looks fresh and new.",
            "天氣在我們日常生活中很重要。春天溫暖多雨。花兒盛開，鳥兒歌唱。很多人喜歡在春天去野餐。夏天非常炎熱。太陽明亮地照耀著。孩子們喜歡吃冰淇淋和在泳池游泳。秋天天氣變涼爽。樹葉變成黃色、橘色和紅色。農夫們收割水果和蔬菜。冬天很冷。山上有時會下雪。人們穿上溫暖的外套和圍巾。我們可以在公園看到漂亮的雪人。每個季節都很特別。我最喜歡春天，因為一切看起來清新嶄新。",
            "medium", "4", "Nature", 130
        );
        a4.addQuestion(new ReadingQuestion("What is the weather like in spring?", "Cold and snowy", "Warm and rainy", "Hot and dry", "Cool and windy", "B", 1));
        a4.addQuestion(new ReadingQuestion("What do children love to do in summer?", "Go skiing", "Eat ice cream and swim", "Build snowmen", "Pick apples", "B", 2));
        a4.addQuestion(new ReadingQuestion("What color do leaves turn in autumn?", "Green", "Blue", "Yellow, orange, and red", "White", "C", 3));
        a4.addQuestion(new ReadingQuestion("What do people wear in winter?", "T-shirts and shorts", "Swimsuits", "Warm coats and scarves", "Raincoats", "C", 4));
        a4.addQuestion(new ReadingQuestion("Which season does the writer like best?", "Summer", "Autumn", "Winter", "Spring", "D", 5));
        articleRepo.save(a4);

        // Article 5: A Trip to the Zoo (Grade 5, Medium)
        Article a5 = new Article(
            "A Trip to the Zoo",
            "動物園之旅",
            "Last Sunday, my family and I went to the zoo. The zoo is big and beautiful. We saw many different animals there. First, we visited the monkey area. The monkeys were very funny. They climbed trees and swung on ropes. Some monkeys ate bananas. Then we went to see the elephants. The elephants were huge! They had long trunks and big ears. One elephant sprayed water with its trunk. It was amazing! After that, we watched the lions. The lions were sleeping in the sun. They looked lazy but powerful. My little sister likes the penguins best. The penguins walked slowly and then jumped into the water. They swam very fast! At noon, we had lunch at the zoo restaurant. I ate a hamburger and drank orange juice. We stayed at the zoo for four hours. It was a wonderful day. I want to visit the zoo again soon.",
            "上週日，我和家人去了動物園。動物園又大又漂亮。我們在那裡看到很多不同的動物。首先，我們參觀了猴子區。猴子非常有趣。牠們爬樹、盪鞦韆。有些猴子在吃香蕉。然後我們去看大象。大象好大！牠們有長長的鼻子和大大的耳朵。一隻大象用鼻子噴水。太神奇了！之後我們看獅子。獅子在陽光下睡覺。牠們看起來懶洋洋但很有力量。我妹妹最喜歡企鵝。企鵝慢慢走路，然後跳進水裡。牠們游得很快！中午我們在動物園餐廳吃午餐。我吃了漢堡，喝了柳橙汁。我們在動物園待了四個小時。這是很棒的一天。我想很快再去動物園。",
            "medium", "5", "Animals", 178
        );
        a5.addQuestion(new ReadingQuestion("When did the family go to the zoo?", "Last Saturday", "Last Sunday", "Last Monday", "Today", "B", 1));
        a5.addQuestion(new ReadingQuestion("What did the monkeys do?", "Swim in the water", "Climb trees and swing on ropes", "Sleep in the sun", "Spray water", "B", 2));
        a5.addQuestion(new ReadingQuestion("Which animal sprayed water with its trunk?", "The lion", "The penguin", "The elephant", "The monkey", "C", 3));
        a5.addQuestion(new ReadingQuestion("What did the lions do when the family saw them?", "They ran around", "They were sleeping in the sun", "They roared loudly", "They played with a ball", "B", 4));
        a5.addQuestion(new ReadingQuestion("How long did the family stay at the zoo?", "Two hours", "Three hours", "Four hours", "Five hours", "C", 5));
        articleRepo.save(a5);

        // Article 6: My Dream Job (Grade 6, Hard)
        Article a6 = new Article(
            "My Dream Job",
            "我的夢想工作",
            "When I grow up, I want to be a scientist. Scientists study the world around us. They ask questions and do experiments to find answers. I am very curious about space. I want to learn about stars, planets, and galaxies. Last month, I read a book about astronauts. The book said that astronauts train for many years before they can go to space. They must be strong and healthy. They also need to study math, science, and engineering. I know it is not easy to become a scientist or an astronaut, but I will work hard. Every day, I read science books and watch space videos online. My science teacher, Mr. Wang, always encourages me. He says curiosity and hard work are the keys to success. My parents also support my dream. They bought me a telescope for my birthday. Now I can look at the moon and stars at night. I believe that if I keep trying, my dream will come true one day. Maybe I will even discover a new planet!",
            "我長大後想當科學家。科學家研究我們周圍的世界。他們提出問題並做實驗來尋找答案。我對太空非常好奇。我想了解星星、行星和星系。上個月，我讀了一本關於太空人的書。書上說太空人在上太空之前要訓練很多年。他們必須強壯又健康。他們也需要學習數學、科學和工程。我知道成為科學家或太空人不容易，但我會努力。每天我都讀科學書，並在網上看太空影片。我的科學老師王老師總是鼓勵我。他說好奇心和努力是成功的關鍵。我父母也支持我的夢想。他們買了一台望遠鏡給我當生日禮物。現在我可以在晚上看月亮和星星。我相信如果我一直努力，我的夢想有一天會實現。也許我甚至會發現一顆新行星！",
            "hard", "6", "Career", 198
        );
        a6.addQuestion(new ReadingQuestion("What does the writer want to be when he grows up?", "A teacher", "A doctor", "A scientist", "An artist", "C", 1));
        a6.addQuestion(new ReadingQuestion("What is the writer curious about?", "The ocean", "Space", "Dinosaurs", "History", "B", 2));
        a6.addQuestion(new ReadingQuestion("What subjects must astronauts study?", "Art and music", "History and geography", "Math, science, and engineering", "English and Chinese", "C", 3));
        a6.addQuestion(new ReadingQuestion("Who encourages the writer?", "His mother", "His science teacher Mr. Wang", "His friend", "His brother", "B", 4));
        a6.addQuestion(new ReadingQuestion("What did the writer's parents buy for his birthday?", "A computer", "A telescope", "A bicycle", "A book", "B", 5));
        articleRepo.save(a6);
    }
}
