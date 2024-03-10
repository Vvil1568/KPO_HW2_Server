# Приложение RestaurantApp
## Краткое описание
Мое приложение RestaurantApp разработано для использования на платформе Android  
Приложение состоит из серверной и клиентской части.
## Функционал
В клиентской части реализованы следующие функции:
- регистрация пользователей (посетителей и администраторов)
- авторизация ранее зарегистрированных пользователей (посетителей и администраторов)  
### Для администраторов доступны следующие функции:
- добавление, удаление и редактирование позиций меню (кнопка "Меню")
- статистика (кнопка "Статистика"), которая позволяет администратору узнать
  - сумму выручки за указанный период
  - количество заказов за указанный период
  - рейтинг блюд
  - самое популярное блюдо
  - отзывы, которые были оставлены
- изменение уровня доступа пользователей
### Для пользователей доступны следующие функции:
- оформление заказа
- отмена заказа
- редактирование заказа
- оплата заказа
- проверка статуса заказа
- возможность оставить отзыв о заказанных ранее блюдах.
### Рассмотрим перечисленные выше функции подробнее:
Чтобы зарегистрироваться в приложении, новый пользователь должен нажать на ссылку "Нет аккаунта? Зарегистрируйтесь".  

![img.png](guide/registrationLink.png)

По нажатию на ссылку откроется окно регистрации, в котором пользователь должен указать ФИО (опционально), 
логин (обязательно), пароль (обязательно).

![img.png](guide/registrationMenu.png)

После ввода указанной информации пользователь должен нажать на кнопку "Зарегистрироваться".
Первый пользователь автоматически получает права администратора.
Остальные пользователи автоматически получают права посетителей, которые администратор может изменить на права администратора
по своему усмотрению (см. ниже).

Зарегистрированный пользователь может зайти в приложение с соответствующими правами, указав свой логин и пароль (см. рисунок выше, вкладка "Вход").
На длину логина и пароля ограничения отсутствуют, но они должны быть непусты. После регистрации вход будет выполнен автоматически.

Пользователь с правами администратора имеет перечисленные выше функции.
Для добавления, удаления и редактирования блюд меню администратор должен нажать на кнопку "Меню".

![img.png](guide/adminMenu.png)

#### Меню
По нажатию на кнопку меню администратор увидит список всех блюд меню:

![img.png](guide/allDishesMenu.png)

Для добавления блюд необходимо нажать на знак "+", расположенный в левом нижнем углу.  
Для редактирования блюда необходимо нажать на символ карандаша, расположенный в правой части экрана напротив соответствующего блюда.  
Для удаления блюда необходимо нажать на символ "-", расположенный в правой части экрана напротив соответствующего блюда.  
При добавлении блюда в меню (при нажатии "+") администратор должен заполнить следующие поля:
- название блюда
- описание блюда
- цена блюда (в рублях)
- время приготовления блюда (в миллисекундах)
- фотография блюда (опционально)

![img.png](guide/addDishMenu.png)

После завершения ввода информации о новом блюде необходимо нажать на "Добавить".  
Есть возможность отменить добавление блюда по нажатию на кнопку "Отмена".

При нажатии на знак карандаша администратор попадает в меню редактирования блюда, которое выглядит так же, как и меню добавления блюда,
в котором уже заполнены соответствующие поля и предлагается лишь откорретировать их.

При нажатии на кнопку "-" блюдо сразу удаляется.

После добавления нового блюда, оно отобразится в меню (см. выше).

#### Статистика
По нажатию на кнопку "статистика" администратор попадает на следующее меню:
![img.png](guide/statisticsMenu.png)

В верхней части меню предлагается ввести период, за который администратор хочет увидеть статистику (опционально).
Если период не выбран, то все статистические показатели будут выведены за весь период.

Для ввода даты нужно нажать на "--.--.----". После нажатия откроется календарь, в котором нужно выбрать дату
(при выборе учитывается время 0:00 выбранной даты), после чего нужно нажать на кнопку "Закрыть".

По нажатию нажатию на кнопку "Сумма выручки" появится всплывающее окно, сообщающее суммарную выручку за указанный период.

По нажатию на кнопку "Количество заказов" появится всплывающее окно, сообщающее суммарное количество заказов за указанный период.

По нажатию на кнопку "Самое популярное блюдо" появится всплывающее окно, сообщающее самое популярное блюдо за указанный период.

При нажтии на кнопку "Рейтинг блюд", будет выведен следующий экран:

![img.png](guide/ratingMenu.png)

Значения рейтинга в звездах округляются с точностью до двух знаков после запятой. При этом предполагается, что максимальный рейтинг - пять звезд.

По нажатию на кнопку "Просмотр отзывов" будут выведены отзывы в следующем виде:

![img.png](guide/commentsMenu.png)

#### Редактирование прав пользователя
Третья функция, доступная администратору - это редактирование прав пользователей.
Для этого необходимо нажать на кнопку "Пользователи", по нажатию на которую, администратор попадает на экран следующего вида:

![img.png](guide/userlistMenu.png)

При нажатии на зеленый ключ, пользователю, ранее являвшемуся постетителем будут выданы права администратора.
При нажатии на красный замок, у пользователя, который был администратором, права администратора анулируются.

