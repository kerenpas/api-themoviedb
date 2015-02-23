/*
 *      Copyright (c) 2004-2015 Stuart Boston
 *
 *      This file is part of TheMovieDB API.
 *
 *      TheMovieDB API is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      TheMovieDB API is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with TheMovieDB API.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.omertron.themoviedbapi.methods;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.enumeration.MediaType;
import com.omertron.themoviedbapi.model.Account;
import com.omertron.themoviedbapi.model.StatusCode;
import com.omertron.themoviedbapi.model.list.MovieFavorite;
import com.omertron.themoviedbapi.model.list.TVFavorite;
import com.omertron.themoviedbapi.model.list.UserList;
import com.omertron.themoviedbapi.tools.ApiUrl;
import com.omertron.themoviedbapi.tools.HttpTools;
import com.omertron.themoviedbapi.tools.MethodBase;
import com.omertron.themoviedbapi.tools.MethodSub;
import com.omertron.themoviedbapi.tools.Param;
import com.omertron.themoviedbapi.tools.PostBody;
import com.omertron.themoviedbapi.tools.PostTools;
import com.omertron.themoviedbapi.tools.TmdbParameters;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.yamj.api.common.exception.ApiExceptionType;

/**
 * Class to hold the Account Methods
 *
 * @author stuart.boston
 */
public class TmdbAccount extends AbstractMethod {

    /**
     * Constructor
     *
     * @param apiKey
     * @param httpTools
     */
    public TmdbAccount(String apiKey, HttpTools httpTools) {
        super(apiKey, httpTools);
    }

    /**
     * Get the basic information for an account. You will need to have a valid session id.
     *
     * @param sessionId
     * @return
     * @throws MovieDbException
     */
    public Account getAccount(String sessionId) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).buildUrl(parameters);
        String webpage = httpTools.getRequest(url);

        try {
            return MAPPER.readValue(webpage, Account.class);
        } catch (IOException ex) {
            throw new MovieDbException(ApiExceptionType.MAPPING_FAILED, "Failed to get Account", url, ex);
        }
    }

    /*
     /account/{id}/watchlist/movies Get the list of movies on an accounts watchlist
     /account/{id}/watchlist/tv Get the list of TV series on an accounts watchlist
     /account/{id}/watchlist Add or remove a movie to an accounts watch list
     */
    /**
     * Get all lists of a given user
     *
     * @param sessionId
     * @param accountId
     * @return The lists
     * @throws MovieDbException
     */
    public List<UserList> getUserLists(String sessionId, int accountId) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.LISTS).buildUrl(parameters);
        return processWrapperList(TR_USER_LIST, url, "user list");
    }

    /**
     * Get the list of favorite movies for an account.
     *
     * @param sessionId
     * @param accountId
     * @return
     * @throws MovieDbException
     */
    public List<MovieFavorite> getFavoriteMovies(String sessionId, int accountId) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.FAVORITE_MOVIES).buildUrl(parameters);
        return processWrapperList(TR_MOVIE_FAV, url, "favorite movies");
    }

    /**
     * Get the list of favorite TV series for an account.
     *
     * @param sessionId
     * @param accountId
     * @return
     * @throws MovieDbException
     */
    public List<TVFavorite> getFavoriteTv(String sessionId, int accountId) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.FAVORITE_TV).buildUrl(parameters);
        return processWrapperList(TR_TV_FAV, url, "favorite TV shows");
    }

    /**
     * Add or remove a movie to an accounts favorite list.
     *
     * @param sessionId
     * @param accountId
     * @param mediaType
     * @param mediaId
     * @param setFavorite
     * @return
     * @throws MovieDbException
     */
    public StatusCode modifyFavoriteStatus(String sessionId, int accountId, MediaType mediaType, int mediaId, boolean setFavorite) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);

        String jsonBody = new PostTools()
                .add(PostBody.MEDIA_TYPE, mediaType.toString().toLowerCase())
                .add(PostBody.MEDIA_ID, mediaId)
                .add(PostBody.FAVORITE, setFavorite)
                .build();

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.FAVORITE).buildUrl(parameters);
        String webpage = httpTools.postRequest(url, jsonBody);

        try {
            return MAPPER.readValue(webpage, StatusCode.class);
        } catch (IOException ex) {
            throw new MovieDbException(ApiExceptionType.MAPPING_FAILED, "Failed to set favorite status", url, ex);
        }
    }

    /**
     * Get the list of rated movies (and associated rating) for an account.
     *
     * @param sessionId
     * @param accountId
     * @param page
     * @param sortBy
     * @param language
     * @return
     * @throws MovieDbException
     */
    public List<MovieFavorite> getRatedMovies(String sessionId, int accountId, Integer page, String sortBy, String language) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);
        parameters.add(Param.PAGE, page);
        parameters.add(Param.SORT_BY, sortBy);
        parameters.add(Param.LANGUAGE, language);

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.RATED_MOVIES).buildUrl(parameters);
        return processWrapperList(TR_MOVIE_FAV, url, "rated movies");
    }

    /**
     * Get the list of rated TV shows (and associated rating) for an account.
     *
     * @param sessionId
     * @param accountId
     * @param page
     * @param sortBy
     * @param language
     * @return
     * @throws MovieDbException
     */
    public List<TVFavorite> getRatedTV(String sessionId, int accountId, Integer page, String sortBy, String language) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);
        parameters.add(Param.PAGE, page);
        parameters.add(Param.SORT_BY, sortBy);
        parameters.add(Param.LANGUAGE, language);

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.RATED_TV).buildUrl(parameters);
        return processWrapperList(TR_TV_FAV, url, "rated TV shows");
    }

    /**
     * Get the list of movies on an accounts watch list.
     *
     * @param sessionId
     * @param accountId
     * @param page
     * @param sortBy
     * @param language
     * @return The watch list of the user
     * @throws MovieDbException
     */
    public List<MovieFavorite> getWatchListMovie(String sessionId, int accountId, Integer page, String sortBy, String language) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);
        parameters.add(Param.PAGE, page);
        parameters.add(Param.SORT_BY, sortBy);
        parameters.add(Param.LANGUAGE, language);

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.WATCHLIST_MOVIES).buildUrl(parameters);
        return processWrapperList(TR_MOVIE_FAV, url, "movie watch list");
    }

    /**
     * Get the list of movies on an accounts watch list.
     *
     * @param sessionId
     * @param accountId
     * @param page
     * @param sortBy
     * @param language
     * @return The watch list of the user
     * @throws MovieDbException
     */
    public List<TVFavorite> getWatchListTV(String sessionId, int accountId, Integer page, String sortBy, String language) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);
        parameters.add(Param.PAGE, page);
        parameters.add(Param.SORT_BY, sortBy);
        parameters.add(Param.LANGUAGE, language);

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.WATCHLIST_TV).buildUrl(parameters);
        return processWrapperList(TR_TV_FAV, url, "TV watch list");
    }

    /**
     * Add or remove a movie to an accounts watch list.
     *
     * @param sessionId
     * @param accountId
     * @param movieId
     * @param mediaType
     * @param addToWatchlist
     * @return
     * @throws MovieDbException
     */
    public StatusCode modifyWatchList(String sessionId, int accountId, MediaType mediaType, Integer movieId, boolean addToWatchlist) throws MovieDbException {
        TmdbParameters parameters = new TmdbParameters();
        parameters.add(Param.SESSION, sessionId);
        parameters.add(Param.ID, accountId);

        String jsonBody = new PostTools()
                .add(PostBody.MEDIA_TYPE, mediaType.toString().toLowerCase())
                .add(PostBody.MEDIA_ID, movieId)
                .add(PostBody.WATCHLIST, addToWatchlist)
                .build();

        URL url = new ApiUrl(apiKey, MethodBase.ACCOUNT).setSubMethod(MethodSub.WATCHLIST).buildUrl(parameters);
        String webpage = httpTools.postRequest(url, jsonBody);

        try {
            return MAPPER.readValue(webpage, StatusCode.class);
        } catch (IOException ex) {
            throw new MovieDbException(ApiExceptionType.MAPPING_FAILED, "Failed to modify watch list", url, ex);
        }
    }
}
