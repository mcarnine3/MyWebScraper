package com.carnine

import jodd.http.HttpRequest;
import jodd.jerry.Jerry;



def url = 'https://news.ycombinator.com/item?id=38788326'//args[0];

def comment_list = [];

while (true)
{
    println("Scraping $url");

    // Send the HTTP request to our URL
    def response = HttpRequest.get(url).send();

    // Parse the HTML document into a Jerry DOM object
    def doc = Jerry.of(response.bodyText());

    // Find all comments <tr>s in the main comment table
    def comments = doc.find('table.comment-tree tr.comtr');

    // Iterate over each comment and extract its data
    comments.each
            { def element ->

                def id = element.attr('id');
                def user = element.find('a.hnuser').text();
                def time = element.find('span.age').attr('title');
                def comment = element.find('div.comment').text();
                def parent = element.find('a:contains(parent)').attr('href');

                // Append the data to comment_list
                comment_list.push([id: id, user: user, time: time, comment: comment, parent: parent]);
            }

    // If there is a next link, set the URL and continue the while, otherwise exit
    def next = doc.find('a[rel="next"]').attr('href');
    if (next) url = 'https://news.ycombinator.com/' + next;
    else break;
}

println(comment_list)