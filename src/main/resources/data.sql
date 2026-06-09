-- 分類資料 (Prepedu 國小英文單字)
INSERT INTO categories (name, name_en, icon, image, sort_order) VALUES
('數字', 'Numbers', 'ti-number', '/images/數字.jpg', 1),
('人物', 'People', 'ti-user', '/images/人物.jpg', 2),
('身體', 'Body', 'ti-body-scan', NULL, 3),
('服飾', 'Clothing', 'ti-shirt', '/images/服飾.jpg', 4),
('動物', 'Animals', 'ti-paw', NULL, 5),
('家庭用品', 'Household', 'ti-home', '/images/家庭用品.jpg', 6),
('食物飲品', 'Food', 'ti-cookie', NULL, 7),
('地方', 'Places', 'ti-map-pin', '/images/地方.jpg', 8),
('時間日期', 'Time', 'ti-clock', NULL, 9),
('動詞', 'Verbs', 'ti-run', '/images/動詞.jpg', 10),
('形容詞', 'Adjectives', 'ti-sparkles', '/images/形容詞.jpg', 11),
('介係詞', 'Prepositions', 'ti-arrows-move', '/images/介係詞.jpg', 12),
('自然天氣', 'Nature', 'ti-sun', '/images/自然天氣.jpg', 13),
('學校學習', 'School', 'ti-school', NULL, 14),
('運動嗜好', 'Sports', 'ti-ball-basketball', NULL, 15),
('職業', 'Occupations', 'ti-briefcase', '/images/職業.jpg', 16),
('尺寸數量', 'Size', 'ti-ruler', NULL, 17),
('節慶', 'Festivals', 'ti-cake', NULL, 18),
('情感思想', 'Emotions', 'ti-mood-smile', '/images/情感思想.jpg', 19),
('家庭', 'Family', 'ti-users', '/images/家庭.jpg', 20);

-- 單字資料：數字
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('one', '一', '/wʌn/', 'I have one apple.', '我有一顆蘋果。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('two', '二', '/tuː/', 'She bought two books.', '她買了兩本書。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('three', '三', '/θriː/', 'There are three cats.', '有三隻貓。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('four', '四', '/fɔːr/', 'We need four chairs.', '我們需要四張椅子。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('five', '五', '/faɪv/', 'He has five pencils.', '他有五支鉛筆。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('six', '六', '/sɪks/', 'The box has six toys.', '盒子裡有六個玩具。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('seven', '七', '/ˈsevən/', 'Seven days make a week.', '七天組成一週。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('eight', '八', '/eɪt/', 'She ate eight cookies.', '她吃了八塊餅乾。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('nine', '九', '/naɪn/', 'The class has nine students.', '班上有九個學生。', (SELECT id FROM categories WHERE name = '數字'), NULL),
('ten', '十', '/ten/', 'My little brother is ten years old.', '我弟弟十歲。', (SELECT id FROM categories WHERE name = '數字'), NULL);

-- 單字資料：人物
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('mother', '媽媽', '/ˈmʌðər/', 'My mother cooks dinner.', '我媽媽煮晚餐。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('father', '爸爸', '/ˈfɑːðər/', 'Father reads me stories.', '爸爸唸故事給我聽。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('sister', '姊妹', '/ˈsɪstər/', 'My sister likes to dance.', '我姊姊喜歡跳舞。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('brother', '兄弟', '/ˈbrʌðər/', 'My brother plays football.', '我哥哥踢足球。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('friend', '朋友', '/frend/', 'Tom is my best friend.', '湯姆是我最好的朋友。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('teacher', '老師', '/ˈtiːtʃər/', 'Our teacher is very kind.', '我們老師很和善。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('student', '學生', '/ˈstuːdənt/', 'Every student should study hard.', '每個學生都應該用功讀書。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('baby', '嬰兒', '/ˈbeɪbi/', 'The baby is sleeping.', '嬰兒在睡覺。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('boy', '男孩', '/bɔɪ/', 'The boy is playing with toys.', '男孩在玩玩具。', (SELECT id FROM categories WHERE name = '人物'), NULL),
('girl', '女孩', '/ɡɜːrl/', 'The girl has long hair.', '女孩有長頭髮。', (SELECT id FROM categories WHERE name = '人物'), NULL);

-- 單字資料：身體
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('head', '頭', '/hed/', 'I wash my head every day.', '我每天洗頭。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('eye', '眼睛', '/aɪ/', 'Her eyes are brown.', '她的眼睛是棕色的。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('nose', '鼻子', '/noʊz/', 'My nose can smell flowers.', '我的鼻子能聞到花香。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('mouth', '嘴巴', '/maʊθ/', 'Open your mouth wide.', '張大你的嘴巴。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('ear', '耳朵', '/ɪr/', 'I can hear with my ears.', '我能用耳朵聽。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('hand', '手', '/hænd/', 'Wash your hands before eating.', '吃飯前要洗手。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('foot', '腳', '/fʊt/', 'My foot hurts.', '我的腳痛。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('arm', '手臂', '/ɑːrm/', 'He raised his arm high.', '他高舉手臂。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('leg', '腿', '/leɡ/', 'The cat has four legs.', '貓有四條腿。', (SELECT id FROM categories WHERE name = '身體'), NULL),
('finger', '手指', '/ˈfɪŋɡər/', 'I have ten fingers.', '我有十根手指。', (SELECT id FROM categories WHERE name = '身體'), NULL);

-- 單字資料：服飾
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('shirt', '襯衫', '/ʃɜːrt/', 'Dad wears a white shirt.', '爸爸穿白襯衫。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('pants', '褲子', '/pænts/', 'These pants are too long.', '這條褲子太長了。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('dress', '洋裝', '/dres/', 'She has a beautiful dress.', '她有一件漂亮的洋裝。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('shoes', '鞋子', '/ʃuːz/', 'My shoes are black.', '我的鞋子是黑色的。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('socks', '襪子', '/sɑːks/', 'I need clean socks.', '我需要乾淨的襪子。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('hat', '帽子', '/hæt/', 'The hat keeps me warm.', '帽子讓我保暖。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('coat', '外套', '/koʊt/', 'It''s cold, wear your coat.', '天氣冷，穿上外套。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('skirt', '裙子', '/skɜːrt/', 'The skirt is red.', '裙子是紅色的。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('jacket', '夾克', '/ˈdʒækɪt/', 'My jacket has pockets.', '我的夾克有口袋。', (SELECT id FROM categories WHERE name = '服飾'), NULL),
('tie', '領帶', '/taɪ/', 'Father wears a tie to work.', '爸爸工作時打領帶。', (SELECT id FROM categories WHERE name = '服飾'), NULL);

-- 單字資料：動物
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('cat', '貓', '/kæt/', 'The cat likes to sleep.', '貓喜歡睡覺。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('dog', '狗', '/dɔːɡ/', 'My dog can sit and stay.', '我的狗會坐下和待著。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('bird', '鳥', '/bɜːrd/', 'The bird flies in the sky.', '鳥在天空中飛翔。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('fish', '魚', '/fɪʃ/', 'Fish live in water.', '魚生活在水中。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('cow', '牛', '/kaʊ/', 'The cow gives us milk.', '牛給我們牛奶。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('pig', '豬', '/pɪɡ/', 'The pig is pink.', '豬是粉紅色的。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('horse', '馬', '/hɔːrs/', 'The horse runs fast.', '馬跑得很快。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('rabbit', '兔子', '/ˈræbɪt/', 'The rabbit has long ears.', '兔子有長耳朵。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('duck', '鴨子', '/dʌk/', 'The duck swims in the pond.', '鴨子在池塘裡游泳。', (SELECT id FROM categories WHERE name = '動物'), NULL),
('chicken', '雞', '/ˈtʃɪkɪn/', 'The chicken lays eggs.', '雞會下蛋。', (SELECT id FROM categories WHERE name = '動物'), NULL);

-- 單字資料：家庭用品
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('table', '桌子', '/ˈteɪbəl/', 'We eat dinner at the table.', '我們在桌子上吃晚餐。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('chair', '椅子', '/tʃer/', 'Please sit on the chair.', '請坐在椅子上。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('bed', '床', '/bed/', 'I sleep in my bed.', '我在床上睡覺。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('door', '門', '/dɔːr/', 'Close the door, please.', '請關門。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('window', '窗戶', '/ˈwɪndoʊ/', 'Look out the window.', '往窗外看。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('lamp', '燈', '/læmp/', 'Turn on the lamp.', '把燈打開。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('clock', '時鐘', '/klɑːk/', 'The clock shows three o''clock.', '時鐘顯示三點。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('TV', '電視', '/ˌtiː ˈviː/', 'We watch TV together.', '我們一起看電視。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('phone', '電話', '/foʊn/', 'Answer the phone, please.', '請接電話。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL),
('computer', '電腦', '/kəmˈpjuːtər/', 'I use the computer to study.', '我用電腦學習。', (SELECT id FROM categories WHERE name = '家庭用品'), NULL);

-- 單字資料：食物飲品
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('apple', '蘋果', '/ˈæpəl/', 'An apple a day keeps the doctor away.', '一天一蘋果，醫生遠離我。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('banana', '香蕉', '/bəˈnænə/', 'The banana is yellow.', '香蕉是黃色的。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('orange', '橘子', '/ˈɔːrɪndʒ/', 'I like sweet oranges.', '我喜歡甜橘子。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('bread', '麵包', '/bred/', 'We eat bread for breakfast.', '我們早餐吃麵包。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('milk', '牛奶', '/mɪlk/', 'Milk is good for children.', '牛奶對小孩很好。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('water', '水', '/ˈwɔːtər/', 'Drink water every day.', '每天要喝水。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('rice', '米飯', '/raɪs/', 'Rice is a staple food.', '米飯是主食。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('egg', '蛋', '/eɡ/', 'I eat an egg for breakfast.', '我早餐吃一顆蛋。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('cake', '蛋糕', '/keɪk/', 'The birthday cake is delicious.', '生日蛋糕很好吃。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL),
('juice', '果汁', '/dʒuːs/', 'Orange juice is my favorite.', '橘子汁是我的最愛。', (SELECT id FROM categories WHERE name = '食物飲品'), NULL);

-- 單字資料：地方
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('school', '學校', '/skuːl/', 'I go to school every day.', '我每天上學。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('home', '家', '/hoʊm/', 'I feel safe at home.', '我在家覺得安全。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('park', '公園', '/pɑːrk/', 'Children play in the park.', '小孩在公園玩耍。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('library', '圖書館', '/ˈlaɪbreri/', 'We read books in the library.', '我們在圖書館看書。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('hospital', '醫院', '/ˈhɑːspɪtl/', 'Doctors work in the hospital.', '醫生在醫院工作。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('store', '商店', '/stɔːr/', 'Mother shops at the store.', '媽媽在商店購物。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('restaurant', '餐廳', '/ˈrestərɑːnt/', 'We eat dinner at the restaurant.', '我們在餐廳吃晚餐。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('zoo', '動物園', '/zuː/', 'Many animals live in the zoo.', '很多動物住在動物園。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('beach', '海灘', '/biːtʃ/', 'We build sandcastles at the beach.', '我們在海灘堆沙堡。', (SELECT id FROM categories WHERE name = '地方'), NULL),
('city', '城市', '/ˈsɪti/', 'The city has many buildings.', '城市有很多建築物。', (SELECT id FROM categories WHERE name = '地方'), NULL);

-- 單字資料：時間日期
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('today', '今天', '/təˈdeɪ/', 'Today is a sunny day.', '今天是晴天。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('yesterday', '昨天', '/ˈjestərdeɪ/', 'Yesterday was my birthday.', '昨天是我的生日。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('tomorrow', '明天', '/təˈmɑːroʊ/', 'Tomorrow is Saturday.', '明天是星期六。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('morning', '早晨', '/ˈmɔːrnɪŋ/', 'I wake up in the morning.', '我在早晨醒來。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('afternoon', '下午', '/ˌæftərˈnuːn/', 'We have lunch in the afternoon.', '我們下午吃午餐。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('night', '夜晚', '/naɪt/', 'Stars shine at night.', '星星在夜晚發光。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('week', '週', '/wiːk/', 'There are seven days in a week.', '一週有七天。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('month', '月', '/mʌnθ/', 'January is the first month.', '一月是第一個月。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('year', '年', '/jɪr/', 'This year I am eight.', '今年我八歲。', (SELECT id FROM categories WHERE name = '時間日期'), NULL),
('birthday', '生日', '/ˈbɜːrθdeɪ/', 'My birthday is in May.', '我的生日在五月。', (SELECT id FROM categories WHERE name = '時間日期'), NULL);

-- 單字資料：動詞
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('eat', '吃', '/iːt/', 'I eat breakfast every morning.', '我每天早上吃早餐。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('drink', '喝', '/drɪŋk/', 'Drink water when you''re thirsty.', '渴的時候要喝水。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('sleep', '睡覺', '/sliːp/', 'Children sleep eight hours a night.', '小孩一晚睡八小時。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('run', '跑', '/rʌn/', 'I run to school every day.', '我每天跑步上學。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('walk', '走路', '/wɔːk/', 'We walk in the park.', '我們在公園散步。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('read', '讀', '/riːd/', 'I read books before bed.', '我睡前看書。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('write', '寫', '/raɪt/', 'Students write with pencils.', '學生用鉛筆寫字。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('play', '玩', '/pleɪ/', 'Children play games together.', '小孩一起玩遊戲。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('sing', '唱歌', '/sɪŋ/', 'We sing songs in music class.', '我們在音樂課唱歌。', (SELECT id FROM categories WHERE name = '動詞'), NULL),
('dance', '跳舞', '/dæns/', 'She likes to dance to music.', '她喜歡隨著音樂跳舞。', (SELECT id FROM categories WHERE name = '動詞'), NULL);

-- 單字資料：形容詞
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('big', '大的', '/bɪɡ/', 'The elephant is big.', '大象很大。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('small', '小的', '/smɔːl/', 'The mouse is small.', '老鼠很小。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('good', '好的', '/ɡʊd/', 'She is a good student.', '她是好學生。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('bad', '壞的', '/bæd/', 'Don''t be a bad boy.', '不要做壞孩子。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('happy', '快樂的', '/ˈhæpi/', 'I am happy today.', '我今天很快樂。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('sad', '傷心的', '/sæd/', 'The boy looks sad.', '那個男孩看起來很傷心。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('hot', '熱的', '/hɑːt/', 'The soup is hot.', '湯很熱。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('cold', '冷的', '/koʊld/', 'Ice cream is cold.', '冰淇淋很冷。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('new', '新的', '/nuː/', 'I have a new backpack.', '我有一個新背包。', (SELECT id FROM categories WHERE name = '形容詞'), NULL),
('old', '舊的', '/oʊld/', 'This is my old toy.', '這是我的舊玩具。', (SELECT id FROM categories WHERE name = '形容詞'), NULL);

-- 單字資料：介係詞
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('in', '在...裡面', '/ɪn/', 'The cat is in the box.', '貓在盒子裡。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('on', '在...上面', '/ɑːn/', 'The book is on the table.', '書在桌子上。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('under', '在...下面', '/ˈʌndər/', 'The ball is under the chair.', '球在椅子下面。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('behind', '在...後面', '/bɪˈhaɪnd/', 'The tree is behind the house.', '樹在房子後面。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('next to', '在...旁邊', '/ˈnekst tuː/', 'The dog sits next to me.', '狗坐在我旁邊。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('between', '在...之間', '/bɪˈtwiːn/', 'The cat sits between two dogs.', '貓坐在兩隻狗之間。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('near', '靠近', '/nɪr/', 'The school is near my home.', '學校在我家附近。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('far', '遠的', '/fɑːr/', 'The store is far from here.', '商店離這裡很遠。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('above', '在...上方', '/əˈbʌv/', 'The bird flies above the tree.', '鳥在樹上方飛翔。', (SELECT id FROM categories WHERE name = '介係詞'), NULL),
('below', '在...下方', '/bɪˈloʊ/', 'The fish swims below the boat.', '魚在船下方游泳。', (SELECT id FROM categories WHERE name = '介係詞'), NULL);

-- 單字資料：自然天氣
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('sun', '太陽', '/sʌn/', 'The sun shines brightly.', '太陽明亮地照耀。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('moon', '月亮', '/muːn/', 'The moon comes out at night.', '月亮在夜晚出現。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('star', '星星', '/stɑːr/', 'Stars twinkle in the sky.', '星星在天空中閃爍。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('rain', '雨', '/reɪn/', 'Rain helps plants grow.', '雨水幫助植物生長。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('snow', '雪', '/snoʊ/', 'Snow is white and cold.', '雪是白色而且冷的。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('wind', '風', '/wɪnd/', 'The wind blows the leaves.', '風吹動樹葉。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('cloud', '雲', '/klaʊd/', 'Clouds float in the sky.', '雲朵在天空中飄浮。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('tree', '樹', '/triː/', 'The tree has green leaves.', '樹有綠色的葉子。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('flower', '花', '/ˈflaʊər/', 'The flower smells sweet.', '花聞起來很香。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL),
('grass', '草', '/ɡræs/', 'Grass grows in the garden.', '草在花園裡生長。', (SELECT id FROM categories WHERE name = '自然天氣'), NULL);

-- 單字資料：學校學習
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('book', '書', '/bʊk/', 'I read a book every night.', '我每晚讀一本書。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('pencil', '鉛筆', '/ˈpensəl/', 'Write with a pencil.', '用鉛筆寫字。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('pen', '筆', '/pen/', 'The pen has blue ink.', '這支筆有藍色墨水。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('paper', '紙', '/ˈpeɪpər/', 'Draw on the paper.', '在紙上畫畫。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('desk', '書桌', '/desk/', 'My desk is clean and tidy.', '我的書桌乾淨整齊。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('classroom', '教室', '/ˈklæsruːm/', 'Our classroom has twenty desks.', '我們教室有二十張書桌。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('homework', '作業', '/ˈhoʊmwɜːrk/', 'I do my homework after school.', '我放學後做作業。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('test', '考試', '/test/', 'The test is tomorrow.', '考試在明天。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('lesson', '課程', '/ˈlesən/', 'Today''s lesson is about animals.', '今天的課程是關於動物。', (SELECT id FROM categories WHERE name = '學校學習'), NULL),
('backpack', '背包', '/ˈbækpæk/', 'My backpack is heavy.', '我的背包很重。', (SELECT id FROM categories WHERE name = '學校學習'), NULL);

-- 單字資料：運動嗜好
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('football', '足球', '/ˈfʊtbɔːl/', 'We play football at school.', '我們在學校踢足球。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('basketball', '籃球', '/ˈbæskɪtbɔːl/', 'Basketball is a fun sport.', '籃球是有趣的運動。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('swimming', '游泳', '/ˈswɪmɪŋ/', 'Swimming is good exercise.', '游泳是很好的運動。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('music', '音樂', '/ˈmjuːzɪk/', 'I love listening to music.', '我喜歡聽音樂。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('painting', '繪畫', '/ˈpeɪntɪŋ/', 'Painting is my hobby.', '繪畫是我的嗜好。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('game', '遊戲', '/ɡeɪm/', 'We play a game together.', '我們一起玩遊戲。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('toy', '玩具', '/tɔɪ/', 'The toy is colorful.', '玩具很色彩繽紛。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('bike', '腳踏車', '/baɪk/', 'I ride my bike to the park.', '我騎腳踏車去公園。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('ball', '球', '/bɔːl/', 'The ball is round.', '球是圓的。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL),
('puzzle', '拼圖', '/ˈpʌzəl/', 'This puzzle has 100 pieces.', '這個拼圖有100片。', (SELECT id FROM categories WHERE name = '運動嗜好'), NULL);

-- 單字資料：職業
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('doctor', '醫生', '/ˈdɑːktər/', 'The doctor helps sick people.', '醫生幫助生病的人。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('nurse', '護士', '/nɜːrs/', 'The nurse is very kind.', '護士很親切。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('police', '警察', '/pəˈliːs/', 'The police keep us safe.', '警察保護我們的安全。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('farmer', '農夫', '/ˈfɑːrmər/', 'The farmer grows vegetables.', '農夫種植蔬菜。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('cook', '廚師', '/kʊk/', 'The cook makes delicious food.', '廚師做美味的食物。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('driver', '司機', '/ˈdraɪvər/', 'The driver drives the bus.', '司機開公車。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('pilot', '飛行員', '/ˈpaɪlət/', 'The pilot flies the airplane.', '飛行員駕駛飛機。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('singer', '歌手', '/ˈsɪŋər/', 'The singer has a beautiful voice.', '歌手有美妙的聲音。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('artist', '藝術家', '/ˈɑːrtɪst/', 'The artist paints pictures.', '藝術家畫圖。', (SELECT id FROM categories WHERE name = '職業'), NULL),
('worker', '工人', '/ˈwɜːrkər/', 'The worker builds houses.', '工人建造房子。', (SELECT id FROM categories WHERE name = '職業'), NULL);

-- 單字資料：尺寸數量
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('long', '長的', '/lɔːŋ/', 'The river is very long.', '這條河很長。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('short', '短的', '/ʃɔːrt/', 'My hair is short.', '我的頭髮很短。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('tall', '高的', '/tɔːl/', 'The building is tall.', '這棟建築很高。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('wide', '寬的', '/waɪd/', 'The road is wide.', '這條路很寬。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('thick', '厚的', '/θɪk/', 'This book is thick.', '這本書很厚。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('thin', '薄的', '/θɪn/', 'The paper is thin.', '紙很薄。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('heavy', '重的', '/ˈhevi/', 'The box is heavy.', '盒子很重。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('light', '輕的', '/laɪt/', 'The feather is light.', '羽毛很輕。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('many', '很多的', '/ˈmeni/', 'There are many flowers.', '有很多花。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL),
('few', '很少的', '/fjuː/', 'I have few toys.', '我有很少玩具。', (SELECT id FROM categories WHERE name = '尺寸數量'), NULL);

-- 單字資料：節慶
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('Christmas', '聖誕節', '/ˈkrɪsməs/', 'Christmas is in December.', '聖誕節在十二月。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('Halloween', '萬聖節', '/ˌhæloʊˈiːn/', 'Children dress up for Halloween.', '小孩為萬聖節打扮。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('birthday', '生日', '/ˈbɜːrθdeɪ/', 'Today is my birthday.', '今天是我的生日。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('party', '派對', '/ˈpɑːrti/', 'We have a party at home.', '我們在家辦派對。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('gift', '禮物', '/ɡɪft/', 'I give you a gift.', '我給你一個禮物。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('cake', '蛋糕', '/keɪk/', 'The birthday cake is sweet.', '生日蛋糕很甜。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('card', '卡片', '/kɑːrd/', 'I make a card for mother.', '我為媽媽做一張卡片。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('celebration', '慶祝', '/ˌseləˈbreɪʃən/', 'The celebration is wonderful.', '慶祝活動很棒。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('festival', '節日', '/ˈfestɪvəl/', 'Spring festival is important.', '春節很重要。', (SELECT id FROM categories WHERE name = '節慶'), NULL),
('holiday', '假日', '/ˈhɑːlədeɪ/', 'Summer holiday is fun.', '暑假很有趣。', (SELECT id FROM categories WHERE name = '節慶'), NULL);

-- 單字資料：情感思想
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('love', '愛', '/lʌv/', 'I love my family.', '我愛我的家人。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('like', '喜歡', '/laɪk/', 'I like chocolate ice cream.', '我喜歡巧克力冰淇淋。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('hate', '討厭', '/heɪt/', 'I hate rainy days.', '我討厭下雨天。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('angry', '生氣的', '/ˈæŋɡri/', 'Don''t be angry with me.', '不要對我生氣。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('scared', '害怕的', '/skerd/', 'I am scared of spiders.', '我害怕蜘蛛。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('excited', '興奮的', '/ɪkˈsaɪtɪd/', 'I am excited about the trip.', '我對這趟旅行很興奮。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('tired', '累的', '/ˈtaɪərd/', 'I feel tired after running.', '跑步後我覺得累。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('surprised', '驚訝的', '/sərˈpraɪzd/', 'I am surprised by the gift.', '我對這個禮物感到驚訝。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('worried', '擔心的', '/ˈwʌrid/', 'Mother is worried about me.', '媽媽為我擔心。', (SELECT id FROM categories WHERE name = '情感思想'), NULL),
('proud', '驕傲的', '/praʊd/', 'I am proud of my work.', '我為我的作品感到驕傲。', (SELECT id FROM categories WHERE name = '情感思想'), NULL);

-- 單字資料：家庭
INSERT INTO vocabularies (english, chinese, phonetic, example_en, example_cn, category_id, image) VALUES
('family', '家庭', '/ˈfæməli/', 'My family loves me.', '我的家庭愛我。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('parents', '父母', '/ˈperənts/', 'My parents care for me.', '我的父母照顧我。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('grandma', '奶奶/外婆', '/ˈɡrænmɑː/', 'Grandma tells me stories.', '奶奶講故事給我聽。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('grandpa', '爺爺/外公', '/ˈɡrænpɑː/', 'Grandpa plays with me.', '爺爺和我玩。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('uncle', '叔叔/舅舅', '/ˈʌŋkəl/', 'Uncle visits us often.', '叔叔經常來看我們。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('aunt', '阿姨/姑姑', '/ænt/', 'Aunt makes delicious cookies.', '阿姨做美味的餅乾。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('cousin', '表兄弟姊妹', '/ˈkʌzən/', 'My cousin is my age.', '我的表兄弟和我同歲。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('pet', '寵物', '/pet/', 'Our pet dog is friendly.', '我們的寵物狗很友善。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('house', '房子', '/haʊs/', 'Our house has a garden.', '我們的房子有花園。', (SELECT id FROM categories WHERE name = '家庭'), NULL),
('room', '房間', '/ruːm/', 'My room is clean.', '我的房間很乾淨。', (SELECT id FROM categories WHERE name = '家庭'), NULL);
